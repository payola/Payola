package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Plugin(
        id: String,
        name: String)
    extends cz.payola.domain.entities.Plugin(id, name)
    with KeyedEntity[String]
    with PersistableEntity
{
}
