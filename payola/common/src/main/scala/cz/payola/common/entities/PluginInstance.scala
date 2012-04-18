package cz.payola.common.entities

import scala.collection.immutable

trait PluginInstance extends Entity
{
    /** Type of the plugin the current object is instance of. */
    type PluginType <: Plugin

    /** Type of the parameter instances the plugin instance. */
    type ParameterInstanceType <: ParameterInstance[_]

    protected val _plugin: PluginType

    protected val _parameterInstances: immutable.Seq[ParameterInstanceType]

    def plugin = _plugin

    def parameterInstances: immutable.Seq[ParameterInstanceType] = _parameterInstances
}
