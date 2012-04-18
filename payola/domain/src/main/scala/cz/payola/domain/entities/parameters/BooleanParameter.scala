package cz.payola.domain.entities.parameters

import cz.payola.domain.entities.{ParameterInstance, Parameter}

class BooleanParameter(name: String, defaultValue: Boolean)
    extends Parameter[Boolean](name, defaultValue) with cz.payola.common.entities.parameters.BooleanParameter
{
    def createInstance(value: Boolean): ParameterInstance[Boolean] = {
        new ParameterInstance[Boolean](this, value)
    }
}
