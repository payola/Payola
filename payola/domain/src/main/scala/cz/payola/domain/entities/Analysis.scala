package cz.payola.domain.entities

import scala.collection.mutable
import cz.payola.domain.entities.analyses._
import evaluation.AnalysisEvaluation

class Analysis(name: String, owner: Option[User])
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with ShareableEntity
    with cz.payola.common.entities.Analysis
{
    type PluginInstanceType = PluginInstance

    type PluginInstanceBindingType = PluginInstanceBinding

    protected var _name = name

    protected val _owner = owner

    protected var _isPublic = false

    protected val _pluginInstances = mutable.ArrayBuffer[PluginInstanceType]()

    protected val _pluginInstanceBindings = mutable.ArrayBuffer[PluginInstanceBindingType]()

    /**
      * Starts evaluation of the analysis.
      * @param timeout Maximal execution time.
      * @return An instance of the [[cz.payola.domain.entities.analyses.evaluation.AnalysisEvaluation]] which can be
      *         queried about analysis evaluation progress and result.
      */
    def evaluate(timeout: Option[Long] = None): AnalysisEvaluation = {
        val evaluation = new AnalysisEvaluation(this, timeout)
        evaluation.start()
        evaluation
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Analysis]
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
            throw new AnalysisException("The analysis contains more than one connected plugin intance component.")
        }
    }

    /**
      * Adds a new plugin instance to the analysis.
      * @param instance The plugin instance to add.
      */
    def addPluginInstance(instance: PluginInstanceType) {
        require(!_pluginInstances.contains(instance), "The instance is already present in the analysis.")
        _pluginInstances += instance
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
        val index = _pluginInstances.indexOf(instance)
        if (index >= 0) {
            _pluginInstances -= instance
            _pluginInstanceBindings --= _pluginInstanceBindings.filter {binding =>
                binding.sourcePluginInstance == instance || binding.targetPluginInstance == instance
            }
            Some(instance)
        } else {
            None
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
      * Returns the plugin instance bindings grouped by the target instances (the instances they go to).
      */
    def pluginInstanceInputBindings: Map[PluginInstance, Seq[PluginInstanceBinding]] = {
        val instanceBindings = pluginInstanceBindings.groupBy(_.targetPluginInstance)
        instanceBindings ++ pluginInstances.filter(i => !instanceBindings.contains(i)).map((_, Nil)).toMap
    }

    /**
      * Returns the plugin instance bindings grouped by the source instances (the instances they come from).
      */
    def pluginInstanceOutputBindings: Map[PluginInstance, Seq[PluginInstanceBinding]] = {
        val instanceBindings = pluginInstanceBindings.groupBy(_.sourcePluginInstance)
        instanceBindings ++ pluginInstances.filter(i => !instanceBindings.contains(i)).map((_, Nil)).toMap
    }

    /**
      * Adds a new plugin instance binding to the analysis.
      * @param binding The plugin instance binding to add.
      */
    def addBinding(binding: PluginInstanceBindingType) {
        require(!_pluginInstanceBindings.contains(binding), "The binding is already present in the analysis.")
        require(_pluginInstances.contains(binding.sourcePluginInstance),
            "The source plugin instance has to be present in the analysis.")
        require(_pluginInstances.contains(binding.targetPluginInstance),
            "The target plugin instance has to be present in the analysis.")

        _pluginInstanceBindings += binding
    }

    /**
      * Collapses the specified binding including the source and the target into one plugin instance. The binding
      * target instance must have exactly one input in order to collapse the binding. The binding source instance must
      * have exactly same number of inputs as the instance that replaces the binding.
      * @param binding The binding to collapse.
      * @param instance The instance that would replace the binding.
      */
    def collapseBinding(binding: PluginInstanceBindingType, instance: PluginInstance) {
        require(_pluginInstanceBindings.contains(binding), "The binding isn't present in the analysis.")
        require(!_pluginInstances.contains(instance), "The instance is already present in the analysis.")
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
      * Adds a new plugin instance binding to the analysis.
      * @param sourcePluginInstance The source plugin instance.
      * @param targetPluginInstance The target plugin instance.
      * @param inputIndex Index of the target plugin instance input the binding is connected to.
      */
    def addBinding(sourcePluginInstance: PluginInstance, targetPluginInstance: PluginInstance, inputIndex: Int = 0) {
        addBinding(new PluginInstanceBinding(sourcePluginInstance, targetPluginInstance, inputIndex))
    }

    /**
      * Removes the specified plugin instance binding from the analysis.
      * @param binding The plugin instance binding to be removed.
      * @return The removed plugin instance binding.
      */
    def removeBinding(binding: PluginInstanceBindingType): Option[PluginInstanceBindingType] = {
        val index = _pluginInstanceBindings.indexOf(binding)
        if (index >= 0) {
            _pluginInstanceBindings -= binding
            Some(binding)
        } else {
            None
        }
    }

    /**
      * Returns the plugin instance whose output is also output of the analysis. If the analysis is valid then
      * [[scala.Some]] is returned.
      */
    def outputInstance: Option[PluginInstance] = {
        pluginInstanceOutputBindings.find(_._2.isEmpty).map(_._1)
    }

    def getLeavesCount : Int = {
        val output = outputInstance

        def countLeaves(plugin: PluginInstance) : Int = {
            val bindings = pluginInstanceBindings.find(_.targetPluginInstance == plugin)
            bindings.size match {
                case 0 => 1
                case _ => bindings.map(binding => {
                    countLeaves(binding.sourcePluginInstance)
                }).sum
            }
        }

        output match {
            case None => 0
            case _ => countLeaves(output.get)
        }
    }
}
