package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins.ParameterValue

class FloatParameterValue(parameter: FloatParameter, value: Float)
    extends ParameterValue[Float](parameter, value)
