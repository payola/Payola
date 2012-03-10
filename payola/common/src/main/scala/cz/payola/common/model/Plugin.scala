package cz.payola.common.model

trait Plugin extends NamedEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    def parameters: Seq[ParameterType]
}
