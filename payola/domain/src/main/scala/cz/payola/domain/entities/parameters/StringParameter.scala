package cz.payola.domain.entities.parameters

import cz.payola.domain.entities.{ParameterInstance, Parameter}

class StringParameter(name: String, defaultValue: String)
    extends Parameter[String](name, defaultValue) with cz.payola.common.entities.parameters.StringParameter
{
    def createInstance(value: String): ParameterInstance[String] = {
        new ParameterInstance[String](this, value)
    }
}
