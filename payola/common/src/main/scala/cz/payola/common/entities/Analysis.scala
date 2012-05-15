package cz.payola.common.entities

import analyses.{PluginInstanceBinding, PluginInstance}
import scala.collection.mutable

/**
  * A named sequence of analytical plugin instances.
  */
trait Analysis extends NamedEntity with OptionallyOwnedEntity with ShareableEntity
{
    /** Type of the analytical plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    /** Type of the bindings between analytical plugin instances. */
    type PluginInstanceBindingType <: PluginInstanceBinding

    protected val _pluginInstances: mutable.Seq[PluginInstanceType]

    protected val _pluginInstanceBindings: mutable.Seq[PluginInstanceBindingType]

    /* Analytical plugin instances the analysis consists of.*/
    def pluginInstances: Seq[PluginInstanceType] = _pluginInstances

    /* Bindings between the analytical plugin instances. */
    def pluginInstanceBindings: Seq[PluginInstanceBindingType] = _pluginInstanceBindings
}
