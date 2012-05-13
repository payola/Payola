package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.Entity

class ParameterValue[A](protected val _parameter: Parameter[A], protected var _value: A)
    extends Entity with cz.payola.common.entities.analyses.ParameterValue[A]
{
    type ParameterType = Parameter[A]
}
