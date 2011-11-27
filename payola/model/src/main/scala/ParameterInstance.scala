package cz.payola.model

import ParameterConstrains._

// TODO: Decide if value may be null...
class ParameterInstance (val parameter: Parameter, v: Any) {
    assert(parameter != null, "Cannot create a parameter instance of a null parameter!")
    
    var _value: Any = null
    setValue(v)

    /** Private method which checks whether the parameter is constrained by this constrain or not constrained at all.
     *
     *  @param constrain Constrain.
     *
     *  @return True or false.
     */
    private def allowsValueOfConstrain(constrain: Int) = {
        if (!parameter.isConstrained)
            true
        else
            parameter.hasValueConstrain(constrain)
    }

    /** Gets a boolean value of the parameter.
     *
     *  @return Boolean value, or false if the value is null.
     *
     *  @throws AssertionError if the parameter constrains don't allow boolean.
     */
    def booleanValue: Boolean = {
        assert(allowsValueOfConstrain(ParameterConstrainBool), "Cannot get boolean value!")

        if (_value == null){
            false
        }else if (_value.isInstanceOf[Boolean]){
            _value.asInstanceOf[Boolean]
        }else if (_value.isInstanceOf[Int]){
            (_value.asInstanceOf[Int] > 0)
        }else if (_value.isInstanceOf[Float]){
            (_value.asInstanceOf[Float] > 0.0)
        }else if (_value.isInstanceOf[String]){
            _value.asInstanceOf[String].toBoolean
        }else{
            assert(false, "Unknown kind of class")
            false
        }

    }

    /** Gets a float value of the parameter.
     *
     *  @return Float value, or 0.0 if the value is null.
     *
     *  @throws AssertionError if the parameter constrains don't allow float.
     */
    def floatValue: Float = {
        assert(allowsValueOfConstrain(ParameterConstrainFloat), "Cannot get float value!")

        if (_value == null){
            0.0.toFloat
        }else if (_value.isInstanceOf[Boolean]){
            if (_value.asInstanceOf[Boolean])
                1.0.toFloat
            else
                0.0.toFloat
        }else if (_value.isInstanceOf[Int]){
            _value.asInstanceOf[Int].toFloat
        }else if (_value.isInstanceOf[Float]){
            _value.asInstanceOf[Float]
        }else if (_value.isInstanceOf[String]){
            _value.asInstanceOf[String].toFloat
        }else{
            assert(false, "Unknown kind of class")
            0.0.toFloat
        }
    }

    /** Gets an int value of the parameter.
     *
     *  @return Int value, or 0 if the value is null.
     *
     *  @throws AssertionError if the parameter constrains don't allow int.
     */
    def intValue: Int = {
        assert(allowsValueOfConstrain(ParameterConstrainInt), "Cannot get int value!")

        if (_value == null){
            0
        }else if (_value.isInstanceOf[Boolean]){
            if (_value.asInstanceOf[Boolean])
                1
            else
                0
        }else if (_value.isInstanceOf[Int]){
            _value.asInstanceOf[Int]
        }else if (_value.isInstanceOf[Float]){
            _value.asInstanceOf[Float].toInt
        }else if (_value.isInstanceOf[String]){
            _value.asInstanceOf[String].toInt
        }else{
            assert(false, "Unknown kind of class")
            0
        }
    }

    /** Boolean value setter.
     *
     * @param bval Boolean value.
     *
     * @throws AssertionError if the parameter constrains don't allow Boolean.
     */
    def setBooleanValue(bval: Boolean) = {
        assert(allowsValueOfConstrain(ParameterConstrainBool), "Cannot set boolean value!")
        setValue(bval)
    }

    /** Int value setter.
     *
     * @param ival Int value.
     *
     * @throws AssertionError if the parameter constrains don't allow int.
     */
    def setIntValue(ival: Int) = {
        assert(allowsValueOfConstrain(ParameterConstrainInt), "Cannot set int value!")
        setValue(ival)
    }

    /** Float value setter.
     *
     * @param fval Float value.
     *
     * @throws AssertionError if the parameter constrains don't allow float.
     */
    def setFloatValue(fval: Float) = {
        assert(allowsValueOfConstrain(ParameterConstrainFloat), "Cannot set float value!")
        setValue(fval)
    }

    /** String value setter.
     *
     * @param strval String value.
     *
     * @throws AssertionError if the parameter constrains don't allow String.
     */
    def setStringValue(strval: String) = {
        assert(allowsValueOfConstrain(ParameterConstrainString), "Cannot set string value!")
        setValue(strval)
    }

    /** Only a convenience method that calls value_=().
     *
     *  @param newVal The new value.
     */
    private def setValue(newVal: Any) = value_=(newVal)

    /** Gets a string value of the parameter.
     *
     *  @return String value, or "" if the value is null.
     *
     *  @throws AssertionError if the parameter constrains don't allow String.
     */
    def stringValue: String = {
        assert(allowsValueOfConstrain(ParameterConstrainString), "Cannot get string value!")

        if (_value == null){
            ""
        }else if (_value.isInstanceOf[Boolean]){
            if (_value.asInstanceOf[Boolean])
                "true"
            else
                "false"
        }else if (_value.isInstanceOf[Int]){
            _value.asInstanceOf[Int].toString
        }else if (_value.isInstanceOf[Float]){
            _value.asInstanceOf[Float].toString
        }else if (_value.isInstanceOf[String]){
            _value.asInstanceOf[String].toString
        }else{
            assert(false, "Unknown kind of class")
            ""
        }
    }

    /** Value getter.
     *
     * @return The value.
     */
    def value: Any = _value

    /** Value setter.
     *
     * @param newVal The new value.
     */
    private def value_=(newVal: Any) = {
        _value = newVal
    }
    

}
