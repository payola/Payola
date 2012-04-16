package cz.payola.data.entities

import org.squeryl.KeyedEntity

class FloatParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Float)
    extends cz.payola.domain.entities.parameters.FloatParameter(id, name, defaultValue)
    with KeyedEntity[String]
{
}


