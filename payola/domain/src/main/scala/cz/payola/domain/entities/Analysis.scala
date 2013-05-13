package cz.payola.domain.entities

import scala.collection.mutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.analyses.evaluation.AnalysisEvaluation
import plugins._
import cz.payola.domain.Entity
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.domain.entities.plugins.concrete._
import parameters._
import scala.Some
import scala.Some

/**
  * @param _name Name of the analysis.
  * @param _owner Owner of the analysis.
  */
class Analysis(protected var _name: String, protected var _owner: Option[User])
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with cz.payola.common.entities.Analysis
{
    checkConstructorPostConditions()

    type PluginInstanceType = PluginInstance

    type PluginInstanceBindingType = PluginInstanceBinding

    type InstanceBindings = Map[PluginInstance, Seq[PluginInstanceBinding]]

    type OntologyCustomizationType = OntologyCustomization

    protected var _pluginInstanceInputBindings: Option[InstanceBindings] = None

    protected var _pluginInstanceOutputBindings: Option[InstanceBindings] = None

    /**
      * Starts evaluation of the analysis.
      * @param timeout Maximal execution time in milliseconds.
      * @return An instance of the [[cz.payola.domain.entities.analyses.evaluation.AnalysisEvaluation]] which can be
      *         queried about the analysis evaluation progress and the result.
      */
    def evaluate(timeout: Option[Long] = None): AnalysisEvaluation = {
        val evaluation = new AnalysisEvaluation(this, timeout)
        evaluation.start()
        evaluation
    }

    def expand(accessibleAnalyses: Seq[Analysis]) {
        pluginInstances.foreach{ i =>
            i.plugin match {
                case a : AnalysisPlugin => {
                    val analysisId = i.getStringParameter("Analysis ID")

                    val remappedParamValues = new mutable.HashMap[String, ParameterValue[_]]()
                    i.parameterValues.filter(_.parameter.name.contains("$")).foreach { p =>
                        remappedParamValues += (p.parameter.name.split("""\$""").apply(1) -> p)
                    }

                    def remapParams {
                        _pluginInstances.foreach{pi =>
                            pi.parameterValues.map { pv =>
                                remappedParamValues.get(pv.id).foreach{ paramVal =>

                                    (pv, paramVal) match {
                                        case (o: StringParameterValue, n: StringParameterValue) => o.value = n.value
                                        case (o: IntParameterValue, n: IntParameterValue) => o.value = n.value
                                        case (o: BooleanParameterValue, n: BooleanParameterValue) => o.value = n.value
                                        case (o: FloatParameterValue, n: FloatParameterValue) => o.value = n.value
                                        case _ =>
                                    }

                                }
                            }
                        }
                    }

                    analysisId.map { idParam =>
                        accessibleAnalyses.find(_.id == idParam).map { analysis =>

                            remapParams

                            analysis.expand(accessibleAnalyses)

                            _pluginInstances ++= analysis.pluginInstances
                            _pluginInstanceBindings ++= analysis.pluginInstanceBindings

                            pluginInstanceBindings.find(_.sourcePluginInstance == i).map { b =>

                                analysis.outputInstance.map{ o =>
                                    _pluginInstanceBindings ++= Seq(new PluginInstanceBinding(o, b.targetPluginInstance, b.targetInputIndex))
                                }
                            }

                            remapParams
                        }
                    }

                    _pluginInstanceBindings --= pluginInstanceBindings.filter(_.sourcePluginInstance == i)
                    _pluginInstances --= Seq(i)
                }
                case _ =>
            }
        }
    }

    /**
      * Checks whether the analysis is valid (i.e. it can be evaluated). If not, then
      * [[cz.payola.domain.entities.analyses.AnalysisException]] is thrown.
      */
    def checkValidity()  {
        if (pluginInstances.isEmpty) {
            throw new AnalysisException("The analysis is empty.")
        }

        // Check input bindings.
        val instanceInvalidBindings = pluginInstanceInputBindings.filter {instanceBindings =>
            val instance = instanceBindings._1
            val bindings = instanceBindings._2
            val x = bindings.map(_.targetInputIndex).distinct
            instance.plugin.inputCount != bindings.length || // Number of input bindings isn't input count.
                bindings.length != x.length // Input has more than one binding.
        }
        if (instanceInvalidBindings.nonEmpty) {
            throw new AnalysisException("The analysis contains plugin instances with invalid input bindings.")
        }

        // Check output bindings.
        if (pluginInstanceOutputBindings.exists(_._2.length > 1)) {
            throw new AnalysisException("The analysis contains plugin instances with invalid output bindings.")
        }

        // Check analysis output.
        val outputInstances = pluginInstanceOutputBindings.filter(_._2.isEmpty).map(_._1).toList
        if (outputInstances.length != 1) {
            throw new AnalysisException("The analysis doesn't contain one output.")
        }
        val outputInstance = outputInstances.head

        // Check cycles.
        val visitedInstances = new mutable.ArrayBuffer[PluginInstance]()
        def visitInstance(instance: PluginInstance) {
            if (visitedInstances.contains(instance)) {
                throw new AnalysisException("The plugin instance bindings contain a cycle.")
            }
            visitedInstances += instance
            pluginInstanceInputBindings(instance).foreach(binding => visitInstance(binding.sourcePluginInstance))
        }
        visitInstance(outputInstance)

        // Check whether the instance graph is connected.
        if (visitedInstances.length != pluginInstances.length) {
            throw new AnalysisException("The analysis contains more than one connected plugin instance component.")
        }

        // Check whether there is a DataFetcher connected to a non SPARQL query plugin.
        val invalidBinding = pluginInstanceBindings.find { b =>
            b.sourcePluginInstance.plugin.isInstanceOf[DataFetcher] &&
            !b.targetPluginInstance.plugin.isInstanceOf[SparqlQuery]
        }
        invalidBinding.foreach { b =>
            throw new AnalysisException(("The analysis contains a data fetcher plugin (%s) that is directly " +
                "connected to a non SPARQL query plugin (%s). That kind of connection isn't currently supported, " +
                "because it would cause a selection of everything from the storage corresponding to the data " +
                "fetcher.").format(b.sourcePluginInstance.plugin.name, b.targetPluginInstance.plugin.name))
        }
    }

    /**
      * Returns the plugin instance bindings grouped by the target instances (the instances they go to).
      */
    def pluginInstanceInputBindings: InstanceBindings = {
        if (_pluginInstanceInputBindings.isEmpty) {
            _pluginInstanceInputBindings = Some(groupBindingsByInstance(_.targetPluginInstance))
        }
        _pluginInstanceInputBindings.get
    }

    /**
      * Returns the plugin instance bindings grouped by the source instances (the instances they come from).
      */
    def pluginInstanceOutputBindings: InstanceBindings = {
        if (_pluginInstanceOutputBindings.isEmpty) {
            _pluginInstanceOutputBindings = Some(groupBindingsByInstance(_.sourcePluginInstance))
        }
        _pluginInstanceOutputBindings.get
    }

    /**
      * Returns the plugin instances that behave as sources of the analysis (they have no inputs).
      */
    def sourceInstances: Seq[PluginInstance] = getInstancesWithoutBindings(pluginInstanceInputBindings)

    /**
      * Returns the plugin instances that behave as outputs of the analysis (they don't have their outputs bound).
      */
    def outputInstances: Seq[PluginInstance] = getInstancesWithoutBindings(pluginInstanceOutputBindings)

    /**
      * Returns the plugin instance whose output is also output of the analysis. If the analysis is valid then
      * [[scala.Some]] is returned.
      */
    def outputInstance: Option[PluginInstance] = outputInstances.headOption

    /**
      * Adds a new plugin instance to the analysis.
      * @param instance The plugin instance to add.
      * @throws IllegalArgumentException if the instance can't be added to the analysis.
      */
    def addPluginInstance(instance: PluginInstanceType) {
        require(!pluginInstances.contains(instance), "The instance is already present in the analysis.")

        invalidatePluginInstanceBindings()
        storePluginInstance(instance)
    }

    /**
      * Adds the specified plugin instances to the analysis.
      * @param instances The plugin instance to add.
      */
    def addPluginInstances(instances: PluginInstanceType*) {
        instances.foreach(i => addPluginInstance(i))
    }

    /**
      * Removes the specified plugin instance and all bindings connected to it from the analysis.
      * @param instance The plugin instance to be removed.
      * @return The removed plugin instance.
      */
    def removePluginInstance(instance: PluginInstanceType): Option[PluginInstanceType] = {
        ifContains(pluginInstances, instance) {
            invalidatePluginInstanceBindings()
            val bindingsToRemove = pluginInstanceBindings.filter { binding =>
                binding.sourcePluginInstance == instance || binding.targetPluginInstance == instance
            }
            removeBindings(bindingsToRemove: _*)
            discardPluginInstance(instance)
        }
    }

    /**
      * Removes the specified plugin instances from the analysis.
      * @param instances The plugin instances to remove.
      */
    def removePluginInstances(instances: PluginInstanceType*) {
        instances.foreach(i => removePluginInstance(i))
    }

    /**
      * Adds a new plugin instance binding to the analysis.
      * @param binding The plugin instance binding to add.
      */
    def addBinding(binding: PluginInstanceBindingType) {
        require(!pluginInstanceBindings.contains(binding), "The binding is already present in the analysis.")
        require(pluginInstances.contains(binding.sourcePluginInstance),
            "The source plugin instance has to be present in the analysis.")
        require(pluginInstances.contains(binding.targetPluginInstance),
            "The target plugin instance has to be present in the analysis.")

        invalidatePluginInstanceBindings()
        storeBinding(binding)
    }

    /**
      * Adds a new plugin instance binding to the analysis.
      * @param sourcePluginInstance The source plugin instance.
      * @param targetPluginInstance The target plugin instance.
      * @param inputIndex Index of the target plugin instance input the binding is connected to.
      */
    def addBinding(sourcePluginInstance: PluginInstance, targetPluginInstance: PluginInstance, inputIndex: Int = 0) {
        addBinding(new PluginInstanceBinding(sourcePluginInstance, targetPluginInstance, inputIndex))
    }

    /**
      * Collapses the specified binding including the source and the target into one plugin instance. The binding
      * target instance must have exactly one input in order to collapse the binding. The binding source instance must
      * have exactly same number of inputs as the instance that replaces the binding.
      * @param binding The binding to collapse.
      * @param instance The instance that would replace the binding.
      */
    def collapseBinding(binding: PluginInstanceBindingType, instance: PluginInstance) {
        require(pluginInstanceBindings.contains(binding), "The binding isn't present in the analysis.")
        require(!pluginInstances.contains(instance), "The instance is already present in the analysis.")
        require(binding.targetPluginInstance.plugin.inputCount == 1, "The binding target instance must have one imput.")
        require(binding.sourcePluginInstance.plugin.inputCount == instance.plugin.inputCount,
            "The binding source instance must have same number of inputs as the instance that replaces the binding.")

        // Store the bindings for later use.
        val sourceInstanceInputBindings = pluginInstanceInputBindings(binding.sourcePluginInstance)
        val targetInstanceOutputBindings = pluginInstanceOutputBindings(binding.targetPluginInstance)

        // Remove the old instances, including the bindings around them.
        removePluginInstances(binding.sourcePluginInstance, binding.targetPluginInstance)

        // Add the new instance and restore the bindings.
        addPluginInstance(instance)
        sourceInstanceInputBindings.foreach(b => addBinding(b.sourcePluginInstance, instance, b.targetInputIndex))
        targetInstanceOutputBindings.foreach(b => addBinding(instance, b.targetPluginInstance, b.targetInputIndex))
    }

    /**
      * Removes the specified plugin instance binding from the analysis.
      * @param binding The plugin instance binding to be removed.
      * @return The removed plugin instance binding.
      */
    def removeBinding(binding: PluginInstanceBindingType): Option[PluginInstanceBindingType] = {
        ifContains(pluginInstanceBindings, binding) {
            invalidatePluginInstanceBindings()
            discardBinding(binding)
        }
    }

    /**
      * Removes the specified plugin instance bindings from the analysis.
      * @param bindings The plugin instance bindings to be removed.
      */
    def removeBindings(bindings: PluginInstanceBindingType*) {
        bindings.foreach(removeBinding(_))
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Analysis]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
    }

    /**
      * Groups the plugin instance bindings to groups indexed by plugin instances.
      * @param f A function that selects the plugin instance, to whose group the plugin instance binding should belong.
      * @return The plugin instance bindings.
      */
    private def groupBindingsByInstance(f: PluginInstanceBinding => PluginInstance): InstanceBindings = {
        val instanceBindings = pluginInstanceBindings.groupBy(f)
        val instancesWithoutBindings = pluginInstances.filter(i => !instanceBindings.contains(i))
        instanceBindings ++ instancesWithoutBindings.map((_, Nil)).toMap
    }

    /**
      * Returns plugin instances with no bindings in the specified map of instance bindings.
      * @param instanceBindings The bindings indexed by the plugin instances.
      * @return The plugin instances without bindings.
      */
    private def getInstancesWithoutBindings(instanceBindings: InstanceBindings): Seq[PluginInstance] = {
        instanceBindings.filter(_._2.isEmpty).map(_._1).toList
    }

    /**
      * Invalidates the plugin instance bindings. Should be called whenever the collection of plugins or plugin
      * instances is altered.
      */
    private def invalidatePluginInstanceBindings() {
        _pluginInstanceInputBindings = None
        _pluginInstanceOutputBindings = None
    }
}
