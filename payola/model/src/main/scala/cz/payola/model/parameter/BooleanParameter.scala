package cz.payola.model.parameter

import cz.payola.common

protected class BooleanParameterInstance(parameter: BooleanParameter, value: Boolean) extends
ParameterInstance[Boolean](parameter, value)
{
    /** Gets a boolean value of the parameter.
      *
      *  @return Boolean value, or false if the value is null.
      */
    override def booleanValue: Boolean = value

    /** Gets a float value of the parameter.
      *
      *  @return Float value, or 0.0 if the value is null.
      */
    override def floatValue: Float = {
        // No need to compare to null
        if (value == false)
            0.0.toFloat
        else
            1.0.toFloat

    }

    /** Gets an int value of the parameter.
      *
      *  @return Int value, or 0 if the value is null.
      */
    override def intValue: Int = {
        // No need to compare to null
        if (value == false)
            0
        else
            1
    }

    /** Boolean value setter.
      *
      * @param bval Boolean value.
      */
    override def setBooleanValue(bval: Boolean) = setValue(bval)

    /** Int value setter.
      *
      * @param ival Int value.
      */
    override def setIntValue(ival: Int) = setBooleanValue(ival != 0)

    /** Float value setter.
      *
      * @param fval Float value.
      */
    override def setFloatValue(fval: Float) = setBooleanValue(fval != 0.0)

    /** String value setter.
      *
      * @param strval String value.
      */
    override def setStringValue(strval: String) = {
        if (strval == "true" || strval == "YES" || strval == "yes" ||
            strval == "1" || strval == "Y" || strval == "y")
            setBooleanValue(true)
        else
            setBooleanValue(false)
    }

    /** Gets a string value of the parameter.
      *
      *  @return String value, or "" if the value is null.
      */
    override def stringValue: String = {
        if (value)
            "true"
        else
            "false"
    }
}

class BooleanParameter(n: String, defaultValue: Boolean) extends
    Parameter[Boolean](n, defaultValue) with common.model.parameters.BooleanParameter
{
    /** Returns a new BooleanParameterInstance instance with the value passed as a parameter of this method.
     *
     * @param newValue The value.
     *
     * @return New BooleanParameterInstance instance.
     */
    override def instanceWithValue(newValue: Boolean): ParameterInstance[Boolean] = {
        new BooleanParameterInstance(this, newValue)
    }
}
