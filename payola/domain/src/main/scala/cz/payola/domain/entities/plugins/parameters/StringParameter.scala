package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins._

class StringParameter(name: String, defaultValue: String)
    extends Parameter[String](name, defaultValue) with cz.payola.common.entities.plugins.parameters.StringParameter
{
    def createValue(value: String): ParameterValue[String] = {
        new StringParameterValue(this, value)
    }
}
