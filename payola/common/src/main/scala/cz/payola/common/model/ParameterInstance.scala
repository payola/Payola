package cz.payola.common.model


trait ParameterInstance[A] extends ModelObject {
    var value: A

    def booleanValue: Boolean
    def floatValue: Float
    def intValue: Int

    def setBooleanValue(bval: Boolean): Unit
    def setIntValue(ival: Int): Unit
    def setFloatValue(fval: Float): Unit
    def setStringValue(strval: String): Unit
    def stringValue: String

    /** Only a convenience method that calls value_=().
      *
      *  @param newVal The new value.
      */
    def setValue(newVal: A) = value = newVal

}
