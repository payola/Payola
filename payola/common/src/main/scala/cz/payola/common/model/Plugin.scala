package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

trait Plugin extends NamedEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    protected val _parameters: mutable.Seq[ParameterType]

    def parameters: immutable.Seq[ParameterType] = _parameters
}
