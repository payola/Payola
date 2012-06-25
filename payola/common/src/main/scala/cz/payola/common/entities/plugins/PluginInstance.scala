package cz.payola.common.entities.plugins

import scala.collection.immutable
import cz.payola.common.entities._

/**
  * An instance of an analytical plugin. That is an evaluation of the plugin parameter values.
  */
trait PluginInstance extends Entity with DescribedEntity
{
    /** Type of the plugin the current object is instance of. */
    type PluginType <: Plugin

    protected val _plugin: PluginType

    protected val _parameterValues: immutable.Seq[PluginType#ParameterValueType]

    /** The corresponding analytical plugin. */
    def plugin = _plugin

    /** The plugin parameter values. */
    def parameterValues: immutable.Seq[PluginType#ParameterValueType] = _parameterValues
}
