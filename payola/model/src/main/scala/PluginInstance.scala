package cz.payola.model

import cz.payola.model.parameter._
import collection.mutable._

class PluginInstance (val plugin: Plugin) {
    require(plugin != null, "Cannot create a plugin instance of a null plugin!")

    // A hash map matching parameters -> values
    private val _parameterValues: HashMap[Parameter[_], ParameterInstance[_]] =
                                        new HashMap[Parameter[_], ParameterInstance[_]]()

    /** Returns an array of parameter instances.
     *
     * @return An array of parameter instances.
     */
    def allValues: Array[ParameterInstance[_]] = _parameterValues.values.toArray

    /** Returns whether a value for that particular parameter has been set or not.
     *
     * @param p Parameter.
     *
     * @return True or false.
     *
     * @throws IllegalArgumentException if p is null or if the plugin doesn't contain such a parameter.
     */
    def hasSetValueForParameter(p: Parameter[_]): Boolean = {
        require(p != null, "Cannot ask about null parameter!")
        require(plugin.containsParameter(p), "The plugin doesn't contain such a parameter")
        !_parameterValues.get(p).isEmpty
    }

    /** Sets a parameter instance for parameter.
     *
     * @param p The parameter.
     * @param v The parameter instance.
     *
     * @throws IllegalArgumentException if either of the parameter is null or if the plugin doesn't contain such
     *          a parameter.
     */
    def setValueForParameter(p: Parameter[_], v: ParameterInstance[_]) = {
        require(p != null, "Cannot set null parameter")
        require(plugin.containsParameter(p), "The plugin doesn't contain such a parameter")
        require(v != null, "Cannot set null value")

        _parameterValues.put(p, v)
    }

    /** Gets a parameter instance for that particular parameter.
     *
     * @param p The parameter.
     *
     * @return An Option with the value.
     *
     * @throws IllegalArgumentException if the parameter is null or if the plugin doesn't contain such a parameter.
     */
    def valueForParameter(p: Parameter[_]): Option[ParameterInstance[_]] = {
        require(p != null, "Cannot ask for null parameter's value!")
        require(plugin.containsParameter(p), "The parameter must be contained by the plugin!")

        _parameterValues.get(p)
    }
}
