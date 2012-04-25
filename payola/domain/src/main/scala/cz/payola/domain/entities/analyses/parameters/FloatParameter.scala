package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.{ParameterValue, Parameter}

class FloatParameter(name: String, defaultValue: Float)
    extends Parameter[Float](name, defaultValue) with cz.payola.common.entities.analyses.parameters.FloatParameter
{
    def createValue(value: Float): ParameterValue[Float] = {
        new FloatParameterValue(this, value)
    }
}
