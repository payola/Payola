package cz.payola.model

import cz.payola.common
import generic.{ConcreteOwnedEntity, ConcreteNamedEntity}
import scala.collection.mutable._

class Analysis(n: String, u: User) extends common.model.Analysis with ConcreteNamedEntity with ConcreteOwnedEntity
{
    setName(n)
    setOwner(u)

    type PluginInstanceType = PluginInstance

    // Plugin instances that this analysis consists of.
    private val _pluginInstances: ArrayBuffer[PluginInstance] = new ArrayBuffer[PluginInstance]()

    /** Adds a new plugin instance to the plugin instances array.
     *
     * @param instance The plugin instance.
     *
     * @throws IllegalArgumentException if the plugin instance is null.
     */
    def appendPluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot append null plugin instance!")

        if (!_pluginInstances.contains(instance))
            _pluginInstances += instance
    }

    /** Returns whether that particular plugin instance is contained in the plugin instances array.
     *
     * @param instance The plugin instance.
     *
     * @throws IllegalArgumentException if the plugin instance is null.
     */
    def containsPluginInstance(instance: PluginInstance) = {
        require(instance != null, "Cannot query about null plugin instance!")

        _pluginInstances.contains()
    }

    /** Returns an immutable copy of the plugin instances array.
     *
     * @return An immutable copy of the plugin instances array.
     */
    def pluginInstances = _pluginInstances

    /** Removes all items in the plugin instances array by the array passed as argument.
     *
     * @param instances The array of instances.
     *
     * @throws IllegalArgumentException if the array is null.
     */
    def pluginInstances_=(instances: Seq[PluginInstance]) = {
        require(pluginInstances != null, "Cannot assign a null array!")

        _pluginInstances.clear()
        instances.foreach(_pluginInstances += _)
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
