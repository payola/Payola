package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginInstance, ParameterValue}

class StringParameterValue(
    parameter: cz.payola.domain.entities.analyses.parameters.StringParameter,
    value: String)
    extends cz.payola.domain.entities.analyses.parameters.StringParameterValue(parameter, value)
    with ParameterValue[String]
{
}
