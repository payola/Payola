package cz.payola.domain.entities.parameters

import cz.payola.domain.entities.{ParameterInstance, Parameter}

class IntParameter(name: String, defaultValue: Int)
    extends Parameter[Int](name, defaultValue) with cz.payola.common.entities.parameters.IntParameter
{
    def createInstance(value: Int): ParameterInstance[Int] = {
        new ParameterInstance[Int](this, value)
    }
}
