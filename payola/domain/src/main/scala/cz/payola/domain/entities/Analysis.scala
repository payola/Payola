package cz.payola.domain.entities

import cz.payola.common
import scala.collection.mutable._
import cz.payola.domain.entities.generic.{ConcreteOwnedEntity, ConcreteNamedEntity}

class Analysis(protected var _name: String, protected val _owner: User) extends common.entities.Analysis with
ConcreteNamedEntity with ConcreteOwnedEntity
{
    type PluginInstanceType = PluginInstance

    // Plugin instances that this analysis consists of.
    protected val _pluginInstances: ArrayBuffer[PluginInstanceType] = new ArrayBuffer[PluginInstanceType]()

    /** Adds a new plugin instance to the plugin instances array.
      *
      * @param instance The plugin instance.
      *
      * @throws IllegalArgumentException if the plugin instance is null.
      */
    def appendPluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot append null plugin instance!")

        if (!_pluginInstances.contains(instance)) {
            _pluginInstances += instance
        }
    }

    /** Returns whether that particular plugin instance is contained in the plugin instances array.
      *
      * @param instance The plugin instance.
      *
      * @throws IllegalArgumentException if the plugin instance is null.
      */
    def containsPluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot query about null plugin instance!")

        _pluginInstances.contains(instance)
    }

    /** Removes all items in the plugin instances array by the array passed as argument.
      *
      * @param instances The array of instances.
      *
      * @throws IllegalArgumentException if the array is null.
      */
    def pluginInstances_=(instances: Seq[PluginInstance]) = {
        require(pluginInstances != null, "Cannot assign a null array!")

        _pluginInstances.clear()
        _pluginInstances ++= instances
    }

    /** Removes a plugin instance from the plugin instances array.
      *
      * @param instance The plugin instance to be removed.
      *
      * @throws IllegalArgumentException if the plugin instance is null.
      */
    def removePluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot remove null plugin instance!")

        _pluginInstances -= instance
    }

    /** Convenience method that just calls pluginInstances_=.
      *
      * @param instances The instance array.
      *
      * @throws IllegalArgumentException if the array is null.
      */
    def setPluginInstances(instances: Array[PluginInstance]) = pluginInstances_=(instances)
}
