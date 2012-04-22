package cz.payola.domain.entities.parameters

import cz.payola._
import domain.entities.generic.{ConcreteEntity, ConcreteNamedEntity}

/** A parameter entity at the domain level. All parameters need to be subclasses.
  *
  * @param id Entity ID.
  * @param _name Name of the parameter.
  * @param defaultValue Default value of the parameter.
  * @tparam A Type of the value.
  */
abstract class Parameter[A](
        id: String  = java.util.UUID.randomUUID.toString,
        protected var _name: String,
        private val defaultValue: A)
    extends ConcreteEntity(id)
    with ConcreteNamedEntity
    with common.entities.Parameter[A]
{
    /** Creates a new instance of the particular parameter with value @value or
      * defaultValue if value is empty or null
      *
      * @param value Value. Can be omitted, then default value is used.
      * @return Instance of this parameter.
      */
    def createInstance(value: Option[A] = null) = {
        if (value == null || value.isEmpty) {
            instanceWithValue(defaultValue)
        }
        else {
            instanceWithValue(value.get)
        }
    }

    /** Returns a new ParameterInstance instance (of its subclass, to be precise) with the value passed
      * as a parameter of this method.
      *
      * @param value The value.
      *
      * @return New ParameterInstance instance.
      */
    protected def instanceWithValue(value: A): ParameterInstance[A]
}
