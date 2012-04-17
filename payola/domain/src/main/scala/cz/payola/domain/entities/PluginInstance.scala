package cz.payola.domain.entities

import cz.payola._
import collection.mutable._
import parameters.{Parameter, ParameterInstance}

class PluginInstance(protected val _plugin: Plugin) extends common.entities.PluginInstance with
domain.entities.generic.ConcreteEntity
{
    require(plugin != null, "Cannot create a plugin instance of a null plugin!")

    type PluginType = Plugin

    type ParameterInstanceType = ParameterInstance[_]

    protected val _parameterInstances = new ArrayBuffer[ParameterInstanceType]()

    /** Sets a parameter instance for parameter.
      *
      * @param v The parameter instance.
      *
      * @throws IllegalArgumentException if either of the parameter is null or if the plugin doesn't contain such
      *          a parameter.
      */
    def addParameterInstance(v: ParameterInstanceType) = {
        require(plugin.containsParameter(v.parameter), "The plugin doesn't contain such a parameter")
        require(v != null, "Cannot set null value")

        _parameterInstances += v
    }

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
        _parameterInstances exists {par: ParameterInstanceType => par.parameter == p}
    }


    /** Gets a parameter instance for that particular parameter.
      *
      * @param p The parameter.
      *
      * @return An Option with the value.
      *
      * @throws IllegalArgumentException if the parameter is null or if the plugin doesn't contain such a parameter.
      */
    def valueForParameter(p: Parameter[_]): Option[ParameterInstanceType] = {
        require(p != null, "Cannot ask for null parameter's value!")
        require(plugin.containsParameter(p), "The parameter must be contained by the plugin!")

        _parameterInstances find {par: ParameterInstanceType => par.parameter == p}
    }
}
