package cz.payola.model.parameter

import cz.payola.common

class StringParameter(private val n: String, private val defaultValue: String) extends
    Parameter[String](n, defaultValue) with common.model.parameters.StringParameter
{
    protected class StringParameterInstance(parameter: StringParameter, value: String) extends
        ParameterInstance[String](parameter, value)
    {
        /** Gets a boolean value of the parameter.
         *
         *  @return Boolean value, or false if the value is null.
         */
        override def booleanValue: Boolean = value == "true"

        /** Gets a float value of the parameter.
         *
         *  @return Float value, or 0.0 if the value is null.
         */
        override def floatValue: Float = {
            if (value == null)
                0.0.toFloat
            else{
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
         *  @return Int value, or 0 if the value is null.
         */
        override def intValue: Int = {
            if (value == null)
                0
            else{
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
            if (bval)
                setStringValue("true")
            else
                setStringValue("false")
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
         *  @return String value, or "" if the value is null.
         */
        override def stringValue: String = value
    }

    /** Returns a new StringParameterInstance instance with the value passed as a parameter of this method.
     *
     * @param value The value.
     *
     * @return New StringParameterInstance instance.
     */
    override def instanceWithValue(newValue: String): ParameterInstance[String] = {
        new StringParameterInstance(this, newValue)
    }
}
