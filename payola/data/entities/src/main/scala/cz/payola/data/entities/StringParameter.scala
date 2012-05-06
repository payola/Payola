package cz.payola.data.entities

import org.squeryl.KeyedEntity

class StringParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: String)
    extends cz.payola.domain.entities.parameters.StringParameter(id, name, defaultValue)
    with PersistableEntity
{
}


