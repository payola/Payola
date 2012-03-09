package cz.payola.model.parameter

import cz.payola._

abstract class Parameter[A](n: String, private val defaultValue: A) extends common.model.Parameter[A] with model.generic.ConcreteNamedModelObject {

    setName(n)

    /** Creates a new instance of the particular parameter with value @value or
      * defaultValue if value is empty or null
      *
      * @param value Value. Can be omitted, then default value is used.
      * @return Instance of this parameter.
      */
    def createInstance(value: Option[A] = null) = {
        if (value == null || value.isEmpty)
            instanceWithValue(defaultValue)
        else
            instanceWithValue(value.get)
    }

}
