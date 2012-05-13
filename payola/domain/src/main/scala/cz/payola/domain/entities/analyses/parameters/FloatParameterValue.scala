package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.ParameterValue

class FloatParameterValue(parameter: FloatParameter, value: Float)
    extends ParameterValue[Float](parameter, value)
