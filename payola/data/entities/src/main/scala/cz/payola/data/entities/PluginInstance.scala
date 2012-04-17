package cz.payola.data.entities

import org.squeryl.KeyedEntity

class PluginInstance(
        id: String,
        plugin: Plugin)
    extends cz.payola.domain.entities.PluginInstance(id, plugin)
    with KeyedEntity[String]
    with PersistableEntity
{
}
