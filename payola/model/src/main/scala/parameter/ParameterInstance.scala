package cz.payola.model.parameter

/** A protected class that represents the parameter instance with an actual value. As this,
 * it is abstract, see subclasses.
 */
abstract class ParameterInstance[T](protected var _value: T){

    /** Gets a boolean value of the parameter.
     *
     *  @return Boolean value, or false if the value is null.
     */
    def booleanValue: Boolean

    /** Gets a float value of the parameter.
     *
     *  @return Float value, or 0.0 if the value is null.
     */
    def floatValue: Float

    /** Gets an int value of the parameter.
     *
     *  @return Int value, or 0 if the value is null.
     */
    def intValue: Int

    /** Boolean value setter.
     *
     * @param bval Boolean value.
     */
    def setBooleanValue(bval: Boolean)

    /** Int value setter.
     *
     * @param ival Int value.
     */
    def setIntValue(ival: Int)

    /** Float value setter.
     *
     * @param fval Float value.
     */
    def setFloatValue(fval: Float)

    /** String value setter.
     *
     * @param strval String value.
     */
    def setStringValue(strval: String)

    /** Only a convenience method that calls value_=().
     *
     *  @param newVal The new value.
     */
    def setValue(newVal: T) = value_=(newVal)

    /** Gets a string value of the parameter.
     *
     *  @return String value, or "" if the value is null.
     */
    def stringValue: String

    /** Value getter.
     *
     * @return The value.
     */
    def value: T = _value

    /** Value setter.
     *
     * @param newVal The new value.
     */
    def value_=(newVal: T) = {
        _value = newVal
    }
}

