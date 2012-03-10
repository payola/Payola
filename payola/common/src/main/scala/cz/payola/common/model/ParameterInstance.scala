package cz.payola.common.model

trait ParameterInstance[A] extends Entity
{
    /** Type of the parameter the current object is instance of. */
    type ParameterType <: Parameter[A]
    
    def parameter: ParameterType

    var value: A
}
