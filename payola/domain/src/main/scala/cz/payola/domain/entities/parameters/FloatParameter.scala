package cz.payola.domain.entities.parameters

import cz.payola.domain.entities.{ParameterInstance, Parameter}

class FloatParameter(name: String, defaultValue: Float)
    extends Parameter[Float](name, defaultValue) with cz.payola.common.entities.parameters.FloatParameter
{
    def createInstance(value: Float): ParameterInstance[Float] = {
        new ParameterInstance[Float](this, value)
    }
}
