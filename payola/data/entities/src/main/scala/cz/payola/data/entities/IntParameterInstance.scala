package cz.payola.data.entities

import org.squeryl.KeyedEntity

class IntParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: IntParameter,
        value: Int)
    extends cz.payola.domain.entities.parameters.IntParameterInstance(id, parameter, value)
    with PersistableEntity
{
}
