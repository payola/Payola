package cz.payola.data.entities

import org.squeryl.KeyedEntity

class BooleanParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: BooleanParameter,
        value: Boolean)
    extends cz.payola.domain.entities.parameters.BooleanParameterInstance(id, parameter, value)
    with PersistableEntity
{
}
