package cz.payola.domain.entities

class ParameterInstance[A](protected val _parameter: Parameter[A], protected var _value: A)
    extends Entity with cz.payola.common.entities.ParameterInstance[A]
{
    type ParameterType = Parameter[A]
}
