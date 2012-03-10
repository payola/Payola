package cz.payola.common.model

trait PluginInstance extends Entity
{
    /** Type of the plugin the current object is instance of. */
    type PluginType <: Plugin

    /** Type of the parameter instances the plugin instance. */
    type ParameterInstanceType <: ParameterInstance[_]
    
    def plugin: PluginType

    def parameterInstances: Seq[ParameterInstanceType]
}
