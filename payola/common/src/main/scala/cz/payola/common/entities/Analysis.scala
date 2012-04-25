package cz.payola.common.entities

import scala.collection.mutable
import cz.payola.common.entities.analyses.PluginInstance

/**
  * A named sequence of analytical plugin instances.
  */
trait Analysis extends NamedEntity with OptionallyOwnedEntity with ShareableEntity
{
    /** Type of the analytical plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    protected val _pluginInstances: mutable.Seq[PluginInstanceType]

    /* Analytical plugin instances the analysis consists of. In the evaluation order. */
    def pluginInstances: Seq[PluginInstanceType] = _pluginInstances
}
