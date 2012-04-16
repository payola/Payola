package cz.payola.data.entities

import org.squeryl.KeyedEntity

class BooleanParameter(
        id: String  = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Boolean)
    extends cz.payola.domain.entities.parameters.BooleanParameter(id, name, defaultValue)
    with KeyedEntity[String]
{
}


