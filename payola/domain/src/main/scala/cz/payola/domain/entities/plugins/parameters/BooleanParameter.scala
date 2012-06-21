package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins._

class BooleanParameter(name: String, defaultValue: Boolean)
    extends Parameter[Boolean](name, defaultValue) with cz.payola.common.entities.plugins.parameters.BooleanParameter
{
    def createValue(value: Boolean): ParameterValue[Boolean] = {
        new BooleanParameterValue(this, value)
    }
}
