package cz.payola.data.entities

import org.squeryl.KeyedEntity

class StringParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: StringParameter,
        value: String)
    extends cz.payola.domain.entities.parameters.StringParameterInstance(id, parameter, value)
    with KeyedEntity[String]
{
}
