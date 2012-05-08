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
      * Adds a new plugin instance to the analysis.
      * @param instance The plugin instance to add.
      */
    def addPluginInstance(instance: PluginInstanceType) {
        require(!_pluginInstances.contains(instance), "The instance is already present in the analysis.")
        _pluginInstances += instance
    }

    /**
      * Removes the specified plugin instance and all bindings connected to it from the analysis.
      * @param instance The plugin instance to be removed.
      * @return The removed plugin instance.
      */
    def removePluginInstance(instance: PluginInstanceType): Option[PluginInstanceType] = {
        val index = _pluginInstances.indexOf(instance)
        if (index > 0) {
            _pluginInstances -= instance
            _pluginInstanceBindings --= _pluginInstanceBindings.filter {binding =>
                binding.sourcePluginInstance == instance || binding.targetPluginInstance == instance
            }
            Some(instance)
        } else {
            None
        }
    }

    def pluginInstanceInputBindings: Map[PluginInstance, Seq[PluginInstanceBinding]] = {
        pluginInstanceBindings.groupBy(_.targetPluginInstance)
    }

    def pluginInstanceOutputBindings: Map[PluginInstance, Seq[PluginInstanceBinding]] = {
        pluginInstanceBindings.groupBy(_.sourcePluginInstance)
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
        if (index > 0) {
            _pluginInstanceBindings -= binding
            Some(binding)
        } else {
            None
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
        val instancesWithInvalidInputBindings = pluginInstanceInputBindings.filter {b =>
            b._1.plugin.inputCount == b._2.length && b._2.length == b._2.map(_.targetInputIndex).distinct.length
        }
        if (instancesWithInvalidInputBindings.nonEmpty) {
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
      * Returns the plugin instance whose output is also output of the analysis. If the analysis is valid then
      * [[scala.Some]] is returned.
      */
    def outputInstance: Option[PluginInstance] = {
        pluginInstanceInputBindings.find(_._2.isEmpty).map(_._1)
    }

    /**
      * Starts evaluation of the analysis.
      * @param timeout Maximal execution time.
      * @return An instance of the [[cz.payola.domain.entities.analyses.evaluation.AnalysisEvaluation]] which can be
      *         queried about analysis evaluation progress and result.
      */
    def evaluate(dataSources: Seq[DataSource], timeout: Option[Long] = None): AnalysisEvaluation = {
        val evaluation = new AnalysisEvaluation(this, timeout)
        evaluation.start()
        evaluation
    }
}
