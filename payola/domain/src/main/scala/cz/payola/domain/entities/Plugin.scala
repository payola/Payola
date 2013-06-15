package cz.payola.domain.entities

import collection.immutable
import cz.payola.domain.rdf.Graph
import cz.payola.domain._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.Entity

/**
  * @param _name Name of the plugin.
  * @param _inputCount Count of the plugin inputs.
  * @param _parameters The plugin parameters.
  * @param id ID of the plugin.
  */
abstract class Plugin(
    protected var _name: String,
    protected val _inputCount: Int,
    protected val _parameters: immutable.Seq[Plugin#ParameterType],
    override val id: String = IDGenerator.newId)
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with cz.payola.common.entities.Plugin
{
    // The owner has to be declared before the checkConstructorPostConditions invocation, which verifies it's not null.
    final var _owner: Option[UserType] = None

    checkConstructorPostConditions()

    type ParameterType = Parameter[_]

    type ParameterValueType = ParameterValue[_]

    val originalClassName = cz.payola.common.Entity.getClassName(getClass)

    /**
     * All the plugins have to behave as if they were instances of the plugin class, not their concrete classes.
     */
    override final def className = "Plugin"

    /**
      * Sets the owner of the plugin.
      * @param value The new owner of the plugin.
      */
    final override def owner_=(value: Option[UserType]) {
        _owner = value
        super[OptionallyOwnedEntity].checkInvariants()
    }

    /**
      * Returns a new instance of the plugin with all parameter instances set to default values.
      */
    final def createInstance(): PluginInstance = {
        new PluginInstance(this, parameters.map(_.createValue(None)))
    }

    /**
      * Evaluates the plugin.
      * @param instance The corresponding instance.
      * @param inputs The input graphs.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the (0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit): Graph

    override final def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Plugin]
    }

    /**
      * If there exists an empty input, the [[cz.payola.domain.entities.analyses.AnalysisException]] is thrown.
      * Otherwise returns the input graphs.
      * @param inputs The inputs.
      * @return The input graphs.
      */
    protected final def getDefinedInputs(inputs: IndexedSeq[Option[Graph]]): IndexedSeq[Graph] = {
        if (inputs.exists(_.isEmpty)) {
            throw new PluginException("The plugin requires all inputs to be defined.")
        }
        inputs.map(_.get)
    }

    /**
      * If the parameter is defined, returns result of application of the specified function on the parameter, otherwise
      * throws an [[cz.payola.domain.entities.analyses.PluginException]].
      * @param p The parameter to test.
      * @param f The function that is applied on the defined parameter value.
      * @tparam A Type of the parameter value.
      * @tparam R Type of the function return value.
      * @return The result of application of the function.
      */
    protected final def usingDefined[A, R](p: Option[A])(f: (A) => R): R = {
        p.map(value => f(value)).getOrElse {
            throw new PluginException("The used value isn't defined.")
        }
    }

    /**
      * If the parameters are defined, returns result of application of the specified function on the parameters,
      * otherwise throws an [[cz.payola.domain.entities.analyses.PluginException]].
      * @param p1 The first parameter to test.
      * @param p2 The second parameter to test.
      * @param f The function that is applied on the defined parameter values.
      * @tparam A Type of the first parameter value.
      * @tparam B Type of the second parameter value.
      * @tparam R Type of the function return value.
      * @return The result of application of the function.
      */
    protected final def usingDefined[A, B, R](p1: Option[A], p2: Option[B])(f: (A, B) => R): R = {
        p1.flatMap(value1 => p2.map(value2 => f(value1, value2))).getOrElse {
            throw new PluginException("One of the used values isn't defined.")
        }
    }

    /**
      * If the parameters are defined, returns result of application of the specified function on the parameters,
      * otherwise throws an [[cz.payola.domain.entities.analyses.PluginException]].
      * @param p1 The first parameter to test.
      * @param p2 The second parameter to test.
      * @param p3 The third parameter to test.
      * @param f The function that is applied on the defined parameter values.
      * @tparam A Type of the first parameter value.
      * @tparam B Type of the second parameter value.
      * @tparam C Type of the third parameter value.
      * @tparam R Type of the function return value.
      * @return The result of application of the function.
      */
    protected final def usingDefined[A, B, C, R](p1: Option[A], p2: Option[B], p3: Option[C])(f: (A, B, C) => R): R = {
        p1.flatMap(value1 => p2.flatMap(value2 => p3.map(value3 => f(value1, value2, value3)))).getOrElse {
            throw new PluginException("One of the used values isn't defined.")
        }
    }
    protected final def usingDefined[A, B, C, D, R](p1: Option[A], p2: Option[B], p3: Option[C], p4: Option[D])(f: (A, B, C, D) => R): R = {
        p1.flatMap(value1 => p2.flatMap(value2 => p3.flatMap(value3 => p4.map(value4 => f(value1, value2, value3, value4))))).getOrElse {
            throw new PluginException("One of the used values isn't defined.")
        }
    }

    override protected final def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
        validate(inputCount >= 0, "inputCount", "The inputCount of the plugin must be a non-negative number.")
        validate(parameters != null, "parameters", "The parameters of the plugin mustn't be null.")
        validate(!parameters.contains(null), "parameters", "The parameters of the plugin mustn't contain null.")
    }
}
