package cz.payola.domain.entities

import scala.collection.mutable
import cz.payola.domain.entities.analyses.plugins.SparqlQuery
import cz.payola.domain.entities.analyses._

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
            _pluginInstanceBindings --= _pluginInstanceBindings.filter {binding: PluginInstanceBindingType =>
                binding.sourcePluginInstance == instance || binding.targetPluginInstance == instance
            }
            Some(instance)
        } else {
            None
        }
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
      * Starts evaluation of the analysis.
      * @param dataSources The data sources used to provider the initial rdf data.
      * @param timeout Maximal execution time.
      * @return An instance of the [[cz.payola.domain.entities.analyses.AnalysisEvaluation]] which can be queried about
      *         analysis evaluation progress and result.
      */
    /*def evaluate(dataSources: Seq[DataSource], timeout: Option[Long] = None): AnalysisEvaluation = {
        val evaluation = new AnalysisEvaluation(this, dataSources, timeout)
        evaluation.start()
        evaluation
    }*/
}
