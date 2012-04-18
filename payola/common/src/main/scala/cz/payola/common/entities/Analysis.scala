package cz.payola.common.entities

import scala.collection
import scala.collection.mutable

trait Analysis extends NamedEntity with OptionallyOwnedEntity with ShareableEntity
{
    /** Type of the plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    protected val _pluginInstances: mutable.Seq[PluginInstanceType]

    def pluginInstances: collection.Seq[PluginInstanceType] = _pluginInstances
}
