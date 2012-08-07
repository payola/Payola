package cz.payola.domain.entities.plugins

import collection.immutable
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.Entity

/**
  * @param _plugin The corresponding plugin.
  * @param _parameterValues The plugin parameter values.
  */
class PluginInstance(protected var _plugin: Plugin, protected var _parameterValues: immutable.Seq[ParameterValue[_]])
    extends Entity
    with cz.payola.common.entities.plugins.PluginInstance
{
    checkConstructorPostConditions()

    type PluginType = Plugin
    /**
      * Returns value of a boolean parameter with the specified name or [[scala.None]] if such doesn't exist.
      */
    def getBooleanParameter(parameterName: String): Option[Boolean] = {
        getParameter(parameterName).map {
            case value: Boolean => value
        }
    }

    /**
      * Returns value of a float parameter with the specified name or [[scala.None]] if such doesn't exist.
      */
    def getFloatParameter(parameterName: String): Option[Float] = {
        getParameter(parameterName).map {
            case value: Float => value
        }
    }

    /**
      * Returns value of an integer parameter with the specified name or [[scala.None]] if such doesn't exist.
      */
    def getIntParameter(parameterName: String): Option[Int] = {
        getParameter(parameterName).map {
            case value: Int => value
        }
    }

    /**
      * Returns value of a string parameter with the specified name or [[scala.None]] if such doesn't exist.
      */
    def getStringParameter(parameterName: String): Option[String] = {
        getParameter(parameterName).map {
            case value: String => value
        }
    }

    /**
      * Sets value of a parameter with the specified name.
      */
    def setParameter(parameterName: String, value: Any): PluginInstance = {
        getParameterValue(parameterName).foreach(i => setParameter(i, value))
        this
    }

    /**
      * Sets value of the specified parameter.
      */
    def setParameter(parameter: Parameter[_], value: Any): PluginInstance = {
        getParameterValue(parameter.name).foreach(i => setParameter(i, value))
        this
    }

    /**
      * Updates the specified parameter value.
      */
    def setParameter(parameterValue: ParameterValue[_], value: Any): PluginInstance = {
        require(_parameterValues.contains(parameterValue),
            "The parameter value to set doesn't correspond to the instance")

        parameterValue match {
            case instance: BooleanParameterValue => instance.value = value.asInstanceOf[Boolean]
            case instance: FloatParameterValue => instance.value = value.asInstanceOf[Float]
            case instance: IntParameterValue => instance.value = value.asInstanceOf[Int]
            case instance: StringParameterValue => instance.value = value.asInstanceOf[String]
            case _ => throw new IllegalArgumentException("The value doesn't conform to type of the parameter.")
        }
        this
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[PluginInstance]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        validate(plugin != null, "plugin", "The plugin mustn't be null.")
        validate(parameterValues.map(_.parameter).sortBy(_.name) == plugin.parameters.sortBy(_.name),
            "parameterValues", "The parameter values must correspond to the plugin parameters.")
    }
}
