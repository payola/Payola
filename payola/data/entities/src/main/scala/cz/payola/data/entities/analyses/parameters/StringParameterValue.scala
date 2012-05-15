package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginInstance, ParameterValue}
import cz.payola.data.entities.PersistableEntity

class StringParameterValue(
    parameter: StringParameter,
    value: String)
    extends cz.payola.domain.entities.analyses.parameters.StringParameterValue(parameter, value)
    with PersistableEntity
    with ParameterValue[String]
{
}
