package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins._

class IntParameter(name: String, defaultValue: Int)
    extends Parameter[Int](name, defaultValue) with cz.payola.common.entities.plugins.parameters.IntParameter
{
    def createValue(value: Int): ParameterValue[Int] = {
        new IntParameterValue(this, value)
    }
}
