package cz.payola.common.model

trait PluginInstance extends Entity
{
    /** Type of the plugin the current object is instance of. */
    type PluginType <: Plugin

    /** Type of the parameter instances the plugin instance. */
    type ParameterInstanceType <: ParameterInstance[_]

    protected val _plugin: PluginType

    protected var _parameterInstances: Seq[ParameterInstanceType]
    
    def plugin = _plugin

    def parameterInstances = _parameterInstances
}
