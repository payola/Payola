package cz.payola.domain.entities.analyses

import collection.immutable
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.{ShareableEntity, NamedEntity, Entity}

abstract class Plugin(protected var _name: String, protected val _inputCount: Int,
    protected val _parameters: immutable.Seq[Plugin#ParameterType])
    extends Entity with NamedEntity with ShareableEntity with cz.payola.common.entities.analyses.Plugin
{
    type ParameterType = Parameter[_]

    type ParameterValueType = ParameterValue[_]

    type ProgressReporter = Double => Unit

    protected var _isPublic = false

    /**
      * Returns a new instance of the plugin with all parameter instances set to default values.
      */
    def createInstance(): PluginInstance = {
        new PluginInstance(this, parameters.map(_.createValue(None)))
    }

    /**
      * Returns a plugin parameter with the specified name.
      * @param parameterName Name of the parameter to return.
      */
    def getParameter(parameterName: String): Option[Parameter[_]] = {
        parameters.find(_.name == parameterName)
    }

    /**
      * Evaluates the plugin.
      * @param instance The corresponding instance.
      * @param inputs The input graphs.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the [0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: ProgressReporter): Graph
}
