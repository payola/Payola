package cz.payola.data.entities

import org.squeryl.KeyedEntity

class FloatParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: FloatParameter,
        value: Float)
    extends cz.payola.domain.entities.parameters.FloatParameterInstance(id, parameter, value)
    with PersistableEntity
{
}
