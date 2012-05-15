package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.{ParameterValue, Parameter}

class BooleanParameter(name: String, defaultValue: Boolean)
    extends Parameter[Boolean](name, defaultValue) with cz.payola.common.entities.analyses.parameters.BooleanParameter
{
    def createValue(value: Boolean): ParameterValue[Boolean] = {
        new BooleanParameterValue(this, value)
    }
}
