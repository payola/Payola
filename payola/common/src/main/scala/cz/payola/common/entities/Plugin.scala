package cz.payola.common.entities

import scala.collection.immutable

trait Plugin extends NamedEntity with ShareableEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    protected val _parameters: immutable.Seq[ParameterType]

    def parameters: immutable.Seq[ParameterType] = _parameters
}
