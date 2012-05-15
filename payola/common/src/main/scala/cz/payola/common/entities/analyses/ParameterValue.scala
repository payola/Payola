package cz.payola.common.entities.analyses

import cz.payola.common.entities.Entity

/**
  * An instance of an analytical plugin parameter.
  * @tparam A Type of the parameter value.
  */
trait ParameterValue[A] extends Entity
{
    /** Type of the parameter the current object is instance of. */
    type ParameterType <: Parameter[A]

    protected val _parameter: ParameterType

    protected var _value: A

    /** The parameter corresponding to this parameter value. */
    def parameter = _parameter

    /** Value of the parameter. */
    def value = _value

    /**
      * Sets value of the parameter.
      * @param value The new parameter value.
      */
    def value_=(value: A) {
        _value = value
    }
}
