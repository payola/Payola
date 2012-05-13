package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.{ParameterValue, Parameter}

class IntParameter(name: String, defaultValue: Int)
    extends Parameter[Int](name, defaultValue) with cz.payola.common.entities.analyses.parameters.IntParameter
{
    def createValue(value: Int): ParameterValue[Int] = {
        new IntParameterValue(this, value)
    }
}
