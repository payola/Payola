package cz.payola.model

import scala.collection.mutable._

class Analysis (n: String, u: User) {
    // Analysis owner
    private var _owner: User = null
    setOwner(u)

    // Name of the analysis.
    private var _name: String = null;
    setName(n)

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

    /** Returns whether the user is an owner of this analysis.
     *
     * @param u User.
     *
     * @return True or false.
     */
    def isOwnedByUser(u: User): Boolean = owner == u

    /** Name getter.
     *
     * @return The name.
     */
    def name: String = _name

    /** Name setter. The name mustn't be null or empty.
     *
     * @param n The new name.
     *
     * @throws IllegalArgumentException if the new name is null.
     */
    def name_=(n: String) = {
        require(n != null && n != "", "Analysis has to have a valid name!")

        _name = n
    }

    /** Owner getter.
     *
     * @return The owner.
     */
    def owner: User = _owner

    /** Owner setter. Mustn't be null.
     *
     * @param New owner.
     *
     * @throws IllegalArgumentException if the new user is null.
     */
    def owner_=(u: User) = {
        require(u != null, "Analysis has to have a non-null owner!")
        val oldOwner = _owner

        _owner = u
        _owner.addAnalysis(this)
        
        if (oldOwner != null)
            oldOwner.removeAnalysis(this)
    }

    /** Returns an immutable copy of the plugin instances array.
     *
     * @return An immutable copy of the plugin instances array.
     */
    def pluginInstances: Array[PluginInstance] = _pluginInstances.toArray

    /** Removes all items in the plugin instances array by the array passed as argument.
     *
     * @param instances The array of instances.
     *
     * @throws IllegalArgumentException if the array is null.
     */
    def pluginInstances_=(instances: Array[PluginInstance]) = {
        require(instances != null, "Cannot assign a null array!")

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

    /** Convenience method that just calls name_=.
     *
     * @param n The new name.
     *
     * @throws IllegalArgumentException if the name is null.
     */
    def setName(n: String) = name_=(n)

    /** Convenience method that just calls owner_=.
     *
     * @param u The new owner.
     *
     * @throws IllegalArgumentException if the user is null.
     */
    def setOwner(u: User) = owner_=(u)

    /** Convenience method that just calls pluginInstances_=.
     *
     * @param instances The instance array.
     *
     * @throws IllegalArgumentException if the array is null.
     */
    def setPluginInstances(instances: Array[PluginInstance]) = pluginInstances_=(instances)
}
