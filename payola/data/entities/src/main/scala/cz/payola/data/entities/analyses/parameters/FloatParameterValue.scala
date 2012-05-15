package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginInstance, ParameterValue}
import cz.payola.data.entities.PersistableEntity

class FloatParameterValue(
    parameter: FloatParameter,
    value: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameterValue(parameter, value)
    with PersistableEntity
    with ParameterValue[Float]
{
}
