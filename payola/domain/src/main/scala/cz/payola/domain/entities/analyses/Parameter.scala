package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.Entity

abstract class Parameter[A](protected val _name: String, protected val _defaultValue: A)
    extends Entity with cz.payola.common.entities.analyses.Parameter[A]
{

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Parameter[_]]
    }

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
}
