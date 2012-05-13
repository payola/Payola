package cz.payola.common.entities.analyses

import scala.collection.immutable
import cz.payola.common.entities.{ShareableEntity, NamedEntity}

/**
  * An analytical plugin that takes a [[cz.payola.common.rdf.Graph]], performs particular operations or computations
  * on the graph and returns modified or a completely new [[cz.payola.common.rdf.Graph]].
  */
trait Plugin extends NamedEntity with ShareableEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    /** Type of the parameter values of the plugin instances. */
    type ParameterValueType <: ParameterValue[_]

    protected val _inputCount: Int

    protected val _parameters: immutable.Seq[ParameterType]

    /** Count of the plugin inputs. */
    def inputCount: Int = inputCount

    /** The parameters whose instances are needed during the evaluation. */
    def parameters: immutable.Seq[ParameterType] = _parameters
}
