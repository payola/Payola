package cz.payola.model.parameter

import cz.payola._
import model.generic.ConcreteEntity
import scala2json.annotations.JSONUnnamedClass
import scala2json.traits.JSONSerializationCustomFields
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/** A protected class that represents the parameter instance with an actual value. As this,
 * it is abstract, see subclasses.
 */

@JSONUnnamedClass
abstract class ParameterInstance[A](val parameter: Parameter[A], var value: A) extends common.model.ParameterInstance[A]
    with ConcreteEntity with JSONSerializationCustomFields
{
    type ParameterType = Parameter[A]

    /** Gets a boolean value of the parameter.
     *
     *  @return Boolean value, or false if the value is null.
     */
    def booleanValue: Boolean = {
        throw new NotImplementedException()
    }

    /** Return the names of the fields.
      *
      * @return Iterable collection for the field names.
      */
    def fieldNamesForJSONSerialization(ctx: Any): scala.collection.Iterable[String] = {
        return List("parameter", "value")
    }

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    def fieldValueForKey(ctx: Any, key: String): Any = {
        key match {
            case "parameter" => parameter.id
            case "value" => value
            case _ => null
        }
    }

    /** Gets a float value of the parameter.
     *
     *  @return Float value, or 0.0 if the value is null.
     */
    def floatValue: Float = {
        throw new NotImplementedException()
    }

    /** Gets an int value of the parameter.
     *
     *  @return Int value, or 0 if the value is null.
     */
    def intValue: Int = {
        throw new NotImplementedException()
    }

    /** Boolean value setter.
     *
     * @param bval Boolean value.
     */
    def setBooleanValue(bval: Boolean): Unit = {
        throw new NotImplementedException()
    }

    /** Int value setter.
     *
     * @param ival Int value.
     */
    def setIntValue(ival: Int): Unit = {
        throw new NotImplementedException()
    }

    /** Float value setter.
     *
     * @param fval Float value.
     */
    def setFloatValue(fval: Float): Unit = {
        throw new NotImplementedException()
    }

    /** String value setter.
     *
     * @param strval String value.
     */
    def setStringValue(strval: String): Unit = {
        throw new NotImplementedException()
    }

    /** Gets a string value of the parameter.
     *
     *  @return String value, or "" if the value is null.
     */
    def stringValue: String = {
        throw new NotImplementedException()
    }

    /** Only a convenience method that calls value_=().
      *
      * @param newVal The new value.
      */
    def setValue(newVal: A) = value = newVal
}

