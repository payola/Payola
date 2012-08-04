package s2js.runtime.client

import s2js.compiler.javascript

object `package`
{
    @javascript("""
        if (s2js.runtime.client.js.isDefined(anObject.__class__) && anObject.__class__ != null) {
            return new scala.Some(anObject.__class__);
        } else {
            return scala.None;
        }
    """)
    def classOf(anObject: Any): Option[Class] = None

    def isClassDefined(className: String): Boolean = {
        s2js.runtime.client.js.isDefined(s2js.adapters.js.eval(className))
    }

    private def isInstanceOf(anObject: Any, classFullName: String): Boolean = {
        val classNameIsAny = classFullName == "scala.Any"
        val classNameIsAnyOrAnyVal = classNameIsAny || classFullName == "scala.AnyVal"
        val classNameIsAnyOrAnyRef = classNameIsAny || classFullName == "scala.AnyRef"
        googTypeOf(anObject) match {
            case "undefined" | "null" => false
            case "number" => {
                classFullName match {
                    case "scala.Byte" | "scala.Short" | "scala.Int" | "scala.Long" => js.isInteger(anObject)
                    case "scala.Float" | "scala.Double" => true
                    case _ => classNameIsAnyOrAnyVal
                }
            }
            case "boolean" => classNameIsAnyOrAnyVal || classFullName == "scala.Boolean"
            case "string" => {
                classFullName match {
                    case "scala.Char" => js.isChar(anObject)
                    case "scala.String" => true
                    case _ => classNameIsAnyOrAnyRef
                }
            }
            case "function" => throw new RuntimeException("Type check of a function isn't supported.")
            case "object" if classNameIsAnyOrAnyRef => true
            case _ => classOf(anObject).map(_.isSubClassOrEqual(classFullName)).getOrElse(false)
        }
    }

    private def asInstanceOf(anObject: Any, className: String): Any = {
        // Just check if the conversion is possible. Nothing has to be done with the object.
        if (!isInstanceOf(anObject, className)) {
            throw new ClassCastException("The object '" + anObject.toString + "' can't be casted to " + className + "")
        }
        anObject
    }

    @javascript("return goog.typeOf(anObject);")
    private def googTypeOf(anObject: Any): String = ""
}
