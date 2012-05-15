package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginInstance, ParameterValue}
import cz.payola.data.entities.PersistableEntity

class FloatParameterValue(
    parameter: cz.payola.domain.entities.analyses.parameters.FloatParameter,
    value: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameterValue(parameter, value)
    with ParameterValue[Float]
{
}
