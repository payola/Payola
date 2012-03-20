package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

trait Analysis extends NamedEntity with OwnedEntity
{
    /** Type of the plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    protected val _pluginInstances: mutable.Seq[PluginInstanceType]

    def pluginInstances: immutable.Seq[PluginInstanceType] = _pluginInstances.toList
}
