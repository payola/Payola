package cz.payola.model.parameter

import cz.payola.common

class FloatParameter(private val n: String, private val defaultValue: Float) extends
    Parameter[Float](n, defaultValue) with common.model.parameters.FloatParameter
{
    protected class FloatParameterInstance(parameter: FloatParameter, value: Float) extends
        ParameterInstance[Float](parameter, value)
    {
        /** Gets a boolean value of the parameter.
         *
         *  @return Boolean value, or false if the value is null.
         */
        override def booleanValue: Boolean = value > 0.0f

        /** Gets a float value of the parameter.
         *
         *  @return Float value, or 0.0 if the value is null.
         */
        override def floatValue: Float = value

        /** Gets an int value of the parameter.
         *
         *  @return Int value, or 0 if the value is null.
         */
        override def intValue: Int = value.toInt

        /** Boolean value setter.
         *
         * @param bval Boolean value.
         */
        override def setBooleanValue(bval: Boolean) = {
            if (bval)
                setFloatValue(1.0f)
            else
                setFloatValue(0.0f)
        }

        /** Int value setter.
         *
         * @param ival Int value.
         */
        override def setIntValue(ival: Int) = setFloatValue(ival.toFloat)

        /** Float value setter.
         *
         * @param fval Float value.
         */
        override def setFloatValue(fval: Float) = setValue(fval)

        /** String value setter.
         *
         * @param strval String value.
         */
        override def setStringValue(strval: String) = {
            // strval.toFloat might end up throwing an exception. Anything that cannot be parsed will be converted
            // to zero.
            try {
                setFloatValue(strval.toFloat)
            } catch {
                case e: Exception => setFloatValue(0.0f)
            }
        }

        /** Gets a string value of the parameter.
         *
         *  @return String value, or "" if the value is null.
         */
        override def stringValue: String = value.toString
    }

    /** Returns a new BooleanParameterInstance instance with the value passed as a parameter of this method.
     *
     * @param value The value.
     *
     * @return New BooleanParameterInstance instance.
     */
    override def instanceWithValue(newValue: Float): ParameterInstance[Float] = {
        new FloatParameterInstance(this, newValue)
    }
}
