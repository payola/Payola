package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins._

class FloatParameter(name: String, defaultValue: Float)
    extends Parameter[Float](name, defaultValue) with cz.payola.common.entities.plugins.parameters.FloatParameter
{
    def createValue(value: Float): ParameterValue[Float] = {
        new FloatParameterValue(this, value)
    }
}
