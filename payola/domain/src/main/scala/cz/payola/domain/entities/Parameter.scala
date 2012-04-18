package cz.payola.domain.entities

abstract class Parameter[A](val name: String, private val defaultValue: A)
    extends Entity with cz.payola.common.entities.Parameter[A]
{
    /**
      * Creates a new instance of the parameter with the specified value.
      * @param value Value of the parameter instance. If it's [[scala.None]] then default value is used.
      * @return A new instance of the parameter.
      */
    def createInstance(value: Option[A] = None): ParameterInstance[A] = {
        createInstance(value.getOrElse(defaultValue))
    }

    /**
      * Creates a new instance of the parameter with the specified value.
      * @param value Value of the parameter instance.
      * @return A new instance of the parameter.
      */
    def createInstance(value: A): ParameterInstance[A]
}
