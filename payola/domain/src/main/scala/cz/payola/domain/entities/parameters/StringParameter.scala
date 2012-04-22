package cz.payola.domain.entities.parameters

import cz.payola.common

/** String parameter instance.
  *
  * @param id Entity ID.
  * @param p Parameter.
  * @param v Value.
  */
class StringParameterInstance(
        id: String  = java.util.UUID.randomUUID.toString,
        p: StringParameter,
        v: String)
    extends ParameterInstance[String](id, p, v)
{
    /** Gets a boolean value of the parameter.
      *
      * @return Boolean value, or false if the value is null.
      */
    override def booleanValue: Boolean = value == "true"

    /** Gets a float value of the parameter.
      *
      * @return Float value, or 0.0 if the value is null.
      */
    override def floatValue: Float = {
        if (value == null) {
            0.0.toFloat
        }
        else {
            // strval.toFloat might end up throwing an exception. Anything that cannot be parsed will be converted
            // to zero.
            try {
                value.toFloat
            } catch {
                case e: Exception => 0.0f
            }
        }
    }

    /** Gets an int value of the parameter.
      *
      * @return Int value, or 0 if the value is null.
      */
    override def intValue: Int = {
        if (value == null) {
            0
        }
        else {
            // strval.toInt might end up throwing an exception. Anything that cannot be parsed will be converted
            // to zero.
            try {
                value.toInt
            } catch {
                case e: Exception => 0
            }
        }
    }

    /** Boolean value setter.
      *
      * @param bval Boolean value.
      */
    override def setBooleanValue(bval: Boolean) = {
        if (bval) {
            setStringValue("true")
        }
        else {
            setStringValue("false")
        }
    }

    /** Int value setter.
      *
      * @param ival Int value.
      */
    override def setIntValue(ival: Int) = setStringValue(ival.toString)

    /** Float value setter.
      *
      * @param fval Float value.
      */
    override def setFloatValue(fval: Float) = setStringValue(fval.toString)

    /** String value setter.
      *
      * @param strval String value.
      */
    override def setStringValue(strval: String) = setValue(strval)

    /** Gets a string value of the parameter.
      *
      * @return String value, or "" if the value is null.
      */
    override def stringValue: String = value
}

/** String parameter.
  *
  * @param id Entity ID.
  * @param n Name.
  * @param defaultValue Default value of the parameter.
  */
class StringParameter(
        id: String  = java.util.UUID.randomUUID.toString,
        n: String,
        defaultValue: String)
    extends Parameter[String](id, n, defaultValue)
    with common.entities.parameters.StringParameter
{
    /** Returns a new StringParameterInstance instance with the value passed as a parameter of this method.
      *
      * @param newValue The value.
      *
      * @return New StringParameterInstance instance.
      */
    override def instanceWithValue(newValue: String): ParameterInstance[String] = {
        new StringParameterInstance(p = this, v = newValue)
    }
}
