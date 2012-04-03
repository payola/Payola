package cz.payola.domain.entities

import cz.payola.common
import generic.{ConcreteEntity, ConcreteOwnedEntity, ConcreteNamedEntity}
import scala.collection.mutable._

class Analysis(protected var _name: String, protected val _owner: User)
    extends ConcreteEntity
    with common.entities.Analysis
    with ConcreteNamedEntity
    with ConcreteOwnedEntity
{
    type PluginInstanceType = PluginInstance

    // Plugin instances that this analysis consists of.
    private val _pluginInstanceIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

    protected val _pluginInstances: ArrayBuffer[PluginInstanceType] = new ArrayBuffer[PluginInstanceType]()

    /** Adds a new plugin instance to the plugin instances array.
      *
      * @param instance The plugin instance.
      *
      * @throws IllegalArgumentException if the plugin instance is null.
      */
    def appendPluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot append null plugin instance!")

        if (!_pluginInstanceIDs.contains(instance.id)) {
            _pluginInstanceIDs += instance.id
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

        _pluginInstanceIDs.contains(instance.id)
    }

    /** Returns an immutable copy of the plugin instances array.
      *
      * @return An immutable copy of the plugin instances array.
      */
    /*def pluginInstances = {
        val instances = List[PluginInstanceType]()
        _pluginInstanceIDs foreach { instanceID: String =>
            val inst: Option[PluginInstanceType] = _pluginInstances.get(instanceID)
            if (inst.isEmpty){
                // TODO loading from DB
            }else{
                inst.get :: instances
            }
        }
        instances.reverse
    }*/

    /** Removes all items in the plugin instances array by the array passed as argument.
      *
      * @param instances The array of instances.
      *
      * @throws IllegalArgumentException if the array is null.
      */
    def pluginInstances_=(instances: Seq[PluginInstance]) = {
        require(pluginInstances != null, "Cannot assign a null array!")

        _pluginInstanceIDs.clear()
        _pluginInstances.clear()
        instances foreach {instance =>
            _pluginInstanceIDs += instance.id
            _pluginInstances += instance
        }
    }

    /** Removes a plugin instance from the plugin instances array.
      *
      * @param instance The plugin instance to be removed.
      *
      * @throws IllegalArgumentException if the plugin instance is null.
      */
    def removePluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot remove null plugin instance!")

        _pluginInstanceIDs -= instance.id
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
