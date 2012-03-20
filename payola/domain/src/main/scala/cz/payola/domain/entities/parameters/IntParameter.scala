package cz.payola.domain.entities.parameters

import cz.payola.common

protected class IntParameterInstance(parameter: IntParameter, value: Int) extends
ParameterInstance[Int](parameter, value)
{
    /** Gets a boolean value of the parameter.
      *
      * @return Boolean value, or false if the value is null.
      */
    override def booleanValue: Boolean = value > 0

    /** Gets a float value of the parameter.
      *
      * @return Float value, or 0.0 if the value is null.
      */
    override def floatValue: Float = value.toFloat

    /** Gets an int value of the parameter.
      *
      * @return Int value, or 0 if the value is null.
      */
    override def intValue: Int = value

    /** Boolean value setter.
      *
      * @param bval Boolean value.
      */
    override def setBooleanValue(bval: Boolean) = {
        if (bval) {
            setIntValue(1)
        }
        else {
            setIntValue(0)
        }
    }

    /** Int value setter.
      *
      * @param ival Int value.
      */
    override def setIntValue(ival: Int) = setValue(ival)

    /** Float value setter.
      *
      * @param fval Float value.
      */
    override def setFloatValue(fval: Float) = setIntValue(fval.toInt)

    /** String value setter.
      *
      * @param strval String value.
      */
    override def setStringValue(strval: String) = {
        // strval.toInt might end up throwing an exception. Anything that cannot be parsed will be converted
        // to zero.
        try { {
            setIntValue(strval.toInt)
        }
        } catch {
            case e: Exception => setIntValue(0)
        }
    }

    /** Gets a string value of the parameter.
      *
      * @return String value, or "" if the value is null.
      */
    override def stringValue: String = value.toString
}

class IntParameter(n: String, defaultValue: Int) extends
Parameter[Int](n, defaultValue) with common.entities.parameters.IntParameter
{
    /** Returns a new BooleanParameterInstance instance with the value passed as a parameter of this method.
      *
      * @param newValue The value.
      *
      * @return New BooleanParameterInstance instance.
      */
    override def instanceWithValue(newValue: Int): ParameterInstance[Int] = {
        new IntParameterInstance(this, newValue)
    }
}
