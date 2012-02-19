package s2js.runtime.s2js

import s2js.compiler.NativeJs
import s2js.adapters.goog
import s2js.runtime.scala.NotImplementedException

object `package`
{
    @NativeJs("""
        if (!s2js.isUndefined(anObject.__class__)) {
            return anObject.__class__;
        }
        return null;
    """)
    def classOf(anObject: Any): Class = null

    private def isInstanceOf(anObject: Any, classFullName: String): Boolean = {
        val classNameIsAny = classFullName == "Any"
        val classNameIsAnyOrAnyVal = classNameIsAny || classFullName == "AnyVal"
        val classNameIsAnyOrAnyRef = classNameIsAny || classFullName == "AnyRef"
        goog.typeOf(anObject) match {
            case "undefined" | "null" => false
            case "number" => {
                classFullName match {
                    case "scala.Byte" | "scala.Short" | "scala.Int" | "scala.Long" => isInteger(anObject)
                    case "scala.Float" | "scala.Double" => true
                    case _ => classNameIsAnyOrAnyVal
                }
            }
            case "boolean" => classNameIsAnyOrAnyVal || classFullName == "scala.Boolean"
            case "string" => {
                classFullName match {
                    case "scala.Char" => isChar(anObject)
                    case "scala.String" => true
                    case _ => classNameIsAnyOrAnyRef
                }
            }
            case "function" => throw new NotImplementedException("Type check of a function isn't supported.")
            case "object" if classNameIsAnyOrAnyRef => true
            case _ if classOf(anObject) != null => classOf(anObject).isSubClassOrEqual(classFullName)
            case _ => throw new RuntimeException("Can't determine the type of the object '" + anObject.toString + ".")
        }
    }

    private def asInstanceOf(anObject: Any, className: String): Any = {
        // Just check if the conversion is possible. Nothing has to be done with the object.
        if (!isInstanceOf(anObject, className)) {
            throw new ClassCastException("The object '" + anObject.toString + "' can't be casted to " + className + ".")
        }
        anObject
    }

    @NativeJs("return goog.typeOf(anObject) === 'undefined';")
    def isUndefined(anObject: Any): Boolean = false

    @NativeJs("return anObject % 1 === 0;")
    private def isInteger(anObject: Any): Boolean = false

    @NativeJs("return anObject.length === 1;")
    private def isChar(anObject: Any): Boolean = false
}
