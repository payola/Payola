package cz.payola.data.entities

import org.squeryl.KeyedEntity

class IntParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Int)
    extends cz.payola.domain.entities.parameters.IntParameter(id, name, defaultValue)
    with PersistableEntity
{
}


