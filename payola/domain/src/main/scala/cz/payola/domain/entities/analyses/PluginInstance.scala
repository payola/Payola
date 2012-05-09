package cz.payola.domain.entities.analyses

import scala.collection.immutable
import cz.payola.domain.entities.Entity
import cz.payola.domain.entities.analyses.parameters._

class PluginInstance(protected val _plugin: Plugin,  protected val _parameterValues: immutable.Seq[ParameterValue[_]])
    extends Entity with cz.payola.common.entities.analyses.PluginInstance
{
    // TODO cannot create DB Schema with this check
    // require(plugin != null, "Cannot create a plugin instance of a null plugin!")
    require(parameterValues.map(_.parameter).sortBy(_.name) == plugin.parameters.sortBy(_.name),
        "The instance doesn't contain parameter instances corresponding to the plugin.")

    type PluginType = Plugin

    /**
      * Returns value of a parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getParameter(parameterName: String): Option[Any] = {
        getParameterValue(parameterName).map(_.value)
    }

    /**
      * Returns value of a boolean parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getBooleanParameter(parameterName: String): Option[Boolean] = {
        getParameter(parameterName).map {
            case value: Boolean => value
        }
    }

    /**
      * Returns value of a float parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getFloatParameter(parameterName: String): Option[Float] = {
        getParameter(parameterName).map {
            case value: Float => value
        }
    }

    /**
      * Returns value of an integer parameter with the specified name or [[scala.None.]] if such doesn't exist.
      */
    def getIntParameter(parameterName: String): Option[Int] = {
        getParameter(parameterName).map {
            case value: Int => value
        }
    }

    /**
      * Returns value of a string parameter with the specified name or [[scala.None.]] if such doesn't exist.
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
        getParameterValue(parameter).foreach(i => setParameter(i, value))
        this
    }

    /**
      * Updates the specified parameter value.
      */
    def setParameter(parameterValue: ParameterValue[_], value: Any): PluginInstance = {
        require(_parameterValues.contains(parameterValue))

        parameterValue match {
            case instance: BooleanParameterValue => instance.value = value.asInstanceOf[Boolean]
            case instance: FloatParameterValue => instance.value = value.asInstanceOf[Float]
            case instance: IntParameterValue => instance.value = value.asInstanceOf[Int]
            case instance: StringParameterValue => instance.value = value.asInstanceOf[String]
            case _ => throw new IllegalArgumentException("The value doesn't conform to type of the parameter.")
        }
        this
    }

    private def getParameterValue(parameter: Parameter[_]): Option[ParameterValue[_]] = {
        parameterValues.find(_.parameter == parameter)
    }

    private def getParameterValue(parameterName: String): Option[ParameterValue[_]] = {
        plugin.getParameter(parameterName).flatMap(p => getParameterValue(p))
    }
}
