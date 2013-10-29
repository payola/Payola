package cz.payola.domain.entities.plugins

import cz.payola.domain.entities._
import cz.payola.domain.Entity

/**
  * @param _name Name of the parameter.
  * @param _defaultValue Default value of the parameter.
  * @tparam A Type of the parameter value.
  */
abstract class Parameter[A](protected val _name: String, protected val _defaultValue: A, protected val _ordering: Option[Int])
    extends Entity
    with cz.payola.common.entities.plugins.Parameter[A]
{
    checkConstructorPostConditions()

    /**
      * Creates a new value of the parameter.
      * @param value Value of the parameter. If it's [[scala.None]] then default value is used.
      * @return A new value of the parameter.
      */
    def createValue(value: Option[A] = None): ParameterValue[A] = {
        createValue(value.getOrElse(defaultValue))
    }

    /**
      * Creates a new value of the parameter.
      * @param value Value of the parameter.
      * @return A new value of the parameter.
      */
    def createValue(value: A): ParameterValue[A]

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Parameter[_]]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        validate(name != null && name.trim != "", "name", "The name mustn't be empty.")
    }
}
