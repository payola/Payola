package cz.payola.domain.entities

import plugins.SparqlQueryPlugin
import scala.collection.mutable

class Analysis(
    protected var _name: String,
    protected val _owner: Option[User],
    val initialPluginInstance: PluginInstance)
extends Entity with NamedEntity with OptionallyOwnedEntity with ShareableEntity with cz.payola.common.entities.Analysis
{
    require(initialPluginInstance.plugin.isInstanceOf[SparqlQueryPlugin], "The initial plugin instance has to "
        + "correspond to a sparql query plugin.")

    type PluginInstanceType = PluginInstance

    protected var _isPublic = false

    /**
      * The plugin instances. Note that order of the instances in the collection matters, because during evaluation
      * of the analysis, the plugins are executed in that order.
      */
    protected val _pluginInstances = mutable.ArrayBuffer[PluginInstanceType](initialPluginInstance)

    protected val initialPlugin = initialPluginInstance.plugin.asInstanceOf[SparqlQueryPlugin]

    /**
      * Adds a new plugin instance to the plugin instances array.
      * @param instance The plugin instance to add.
      * @param atIndex Index the added plugin should be added at. Has to be grater or equal to one.
      * @return The added plugin instance.
      * @throws IllegalArgumentException if the plugin instance is null or the atIndex is invalid.
      */
    def addPluginInstance(instance: PluginInstanceType, atIndex: Int): Option[PluginInstanceType] = {
        require(instance != null, "Cannot add null plugin instance.")
        require(atIndex >= 1 && atIndex <= pluginInstances.length, "The atIndex is invalid.")

        if (!_pluginInstances.contains(instance)) {
            _pluginInstances.insert(atIndex, instance)
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
      * Returns the initial query that should be executed on all data sources, which is encapsulated in the first
      * sparql query plugin instance.
      */
    def initialQuery: String = {
        initialPluginInstance.getParameterInstance(initialPlugin.queryParameter).value.toString
    }

    def nonInitialPluginInstances: collection.Seq[PluginInstanceType] = {
        pluginInstances.tail
    }
}
