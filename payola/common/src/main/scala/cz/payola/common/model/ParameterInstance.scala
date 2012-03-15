package cz.payola.common.model

trait ParameterInstance[A] extends Entity
{
    /** Type of the parameter the current object is instance of. */
    type ParameterType <: Parameter[A]

    protected val _parameter: ParameterType

    protected var _value: A

    def parameter = _parameter

    def value = _value

    def value_=(value: A) {
        _value = value
    }
}
