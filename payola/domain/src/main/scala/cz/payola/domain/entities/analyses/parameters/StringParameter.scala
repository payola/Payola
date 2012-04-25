package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.{ParameterValue, Parameter}

class StringParameter(name: String, defaultValue: String)
    extends Parameter[String](name, defaultValue) with cz.payola.common.entities.analyses.parameters.StringParameter
{
    def createValue(value: String): ParameterValue[String] = {
        new StringParameterValue(this, value)
    }
}
