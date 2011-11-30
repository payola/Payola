package cz.payola.model.parameter

class StringParameter(private val n: String, private val defaultValue: String) extends
                                                                            Parameter[String](n, defaultValue) {

    protected class StringParameterInstance(protected val strVal: String) extends
                                                                            ParameterInstance[String](strVal){
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
            else
                value.toFloat
        }

        /** Gets an int value of the parameter.
         *
         *  @return Int value, or 0 if the value is null.
         */
        override def intValue: Int = {
            if (value == null)
                0
            else
                value.toInt
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
        def stringValue: String = value
    }

    /** Returns a new StringParameterInstance instance with the value passed as a parameter of this method.
     *
     * @parameter value The value.
     *
     * @return New StringParameterInstance instance.
     */
    override def instanceWithValue(newValue: String): ParameterInstance[String] = {
        new StringParameterInstance(newValue)
    }
}
