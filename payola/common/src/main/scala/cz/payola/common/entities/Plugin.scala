package cz.payola.common.entities

import scala.collection.immutable
import cz.payola.common.entities.analyses._
import cz.payola.common.entities.plugins._
import cz.payola.common.Entity

/**
  * An analytical plugin that, when evaluated, takes a non-negative number of input graphs, performs particular
  * operations or computations on the input graphs (which may or may not be defined) and returns modified or a
  * completely new [[cz.payola.common.rdf.Graph]].
  */
trait Plugin extends Entity with OptionallyOwnedEntity with NamedEntity with ShareableEntity
{
    /** Type of the parameters of the plugin */
    type ParameterType <: Parameter[_]

    /** Type of the parameter values of the plugin instances. */
    type ParameterValueType <: ParameterValue[_]

    protected val _inputCount: Int

    protected val _parameters: immutable.Seq[ParameterType]

    val originalClassName : String

    /** Count of the plugin inputs. */
    def inputCount: Int = _inputCount

    /** The plugin parameters. */
    def parameters: immutable.Seq[ParameterType] = _parameters

    /**
     * Returns a plugin parameter with the specified name.
     * @param parameterName Name of the parameter to return.
     */
    final def getParameter(parameterName: String): Option[ParameterType] = {
        parameters.find(_.name == parameterName)
    }
}
