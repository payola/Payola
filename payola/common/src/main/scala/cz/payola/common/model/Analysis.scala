package cz.payola.common.model

trait Analysis extends NamedEntity with OwnedEntity
{
    /** Type of the plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    protected val _pluginInstances: Seq[PluginInstanceType]

    def pluginInstances = _pluginInstances
}
