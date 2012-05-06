package cz.payola.domain.entities

import scala.collection.mutable
import cz.payola.domain.entities.analyses.plugins.SparqlQueryPlugin
import cz.payola.domain.entities.analyses._

/** Analysis entity at the domain level.
  *
  * Contains a list of plugin instances.
  *
  * @param name Name of the analysis.
  * @param owner Owner of the analysis.
  */
class Analysis(name: String, owner: Option[User], val initialPluginInstance: PluginInstance)
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with ShareableEntity
    with cz.payola.common.entities.Analysis
{
    require(initialPluginInstance.plugin.isInstanceOf[SparqlQueryPlugin],
        "The initial plugin instance has to correspond to a sparql query plugin.")

    type PluginInstanceType = PluginInstance

    protected var _name = name

    protected val _owner = owner

    protected var _isPublic = false

    /**
      * The plugin instances. Note that order of the instances in the collection matters, because during evaluation
      * of the analysis, the plugins are executed in that order.
      */
    protected val _pluginInstances = mutable.ArrayBuffer[PluginInstanceType](initialPluginInstance)

    /**
      * The initial sparql query plugin whose query is executed on all data sources.
      */
    private val initialPlugin = initialPluginInstance.plugin.asInstanceOf[SparqlQueryPlugin]

    /**
      * Adds a new plugin instance to the analysis.
      * @param instance The plugin instance to add.
      * @param atIndex Index the added plugin should be added at. Has to be grater or equal to one.
      * @return The added plugin instance.
      * @throws IllegalArgumentException if the plugin instance is null or the atIndex is invalid.
      */
    def addPluginInstance(instance: PluginInstanceType, atIndex: Option[Int] = None): Option[PluginInstanceType] = {
        val index = atIndex.getOrElse(pluginInstances.length)
        require(instance != null, "Cannot add null plugin instance.")
        require(index >= 1 && index <= pluginInstances.length, "The atIndex is invalid.")

        if (!_pluginInstances.contains(instance)) {
            _pluginInstances.insert(index, instance)
            Some(instance)
        } else {
            None
        }
    }

    /**
      * Removes the specified plugin instance from the analysis. The initial plugin instance cannot be removed.
      * @param instance The plugin instance to be removed.
      * @return The removed plugin instance.
      */
    def removePluginInstance(instance: PluginInstanceType): Option[PluginInstanceType] = {
        require(instance != initialPluginInstance, "Cannot remove the initial plugin instance.")

        val index = _pluginInstances.indexOf(instance)
        if (index > 0) {
            _pluginInstances -= instance
            Some(instance)
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
    def evaluate(dataSources: Seq[DataSource], timeout: Option[Long] = None): AnalysisEvaluation = {
        val evaluation = new AnalysisEvaluation(this, dataSources, timeout)
        evaluation.start()
        evaluation
    }

    /**
      * Returns the initial query that should be executed on all data sources, which is encapsulated in the first
      * sparql query plugin instance.
      */
    private[entities] def initialQuery: String = {
        val defaultQueryValue = initialPlugin.queryParameter.defaultValue
        initialPluginInstance.getStringParameter(initialPlugin.queryParameter.name).getOrElse(defaultQueryValue)
    }

    /**
      * Returns all plugin instances except for the initial sparql query plugin instance.
      */
    private[entities] def nonInitialPluginInstances: Seq[PluginInstanceType] = {
        pluginInstances.tail
    }
}
