package cz.payola.common.entities

import scala.collection
import scala.collection.mutable

trait Plugin extends NamedEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    protected val _parameters: mutable.Seq[ParameterType]

    def parameters: collection.Seq[ParameterType] = _parameters
}
