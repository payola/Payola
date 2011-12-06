package s2js.runtime.types

import s2js.compiler.Native
import s2js.adapters.goog

object `package` {
    private def isInstanceOf(anObject: Any, className: String): Boolean = {
        val jsType = goog.typeOf(anObject)
        val classNameIsAny = className == "Any"
        val classNameIsAnyOrVal = classNameIsAny || className == "AnyVal"
        val classNameIsAnyOrRef = classNameIsAny || className == "AnyRef"
        jsType match {
            case "undefined" | "null" => false
            case "number" => {
                className match {
                    case "Byte" | "Short" | "Integer" | "Long" => isInteger(anObject)
                    case "Float" | "Double" => true
                    case _ => classNameIsAnyOrVal
                }
            }
            case "boolean" => classNameIsAnyOrVal || className == "Boolean"
            case "string" => {
                className match {
                    case "Char" => isChar(anObject)
                    case "String" => true
                    case _ => classNameIsAnyOrRef
                }
            }
            case "object" if classNameIsAnyOrRef => true
            case "function" => false // TODO
            case _ => isInMetaClassHierarchy(getObjectMetaClass(anObject), className)
        }
    }

    private def isInMetaClassHierarchy(rootMetaClass: MetaClass, metaClassName: String): Boolean = {
        if (goog.typeOf(rootMetaClass) != "object") {
            throw new RuntimeException // TODO message and proper type
        } else if (metaClassName == rootMetaClass.fullName) {
            true
        } else {
            existsParentMetaClass(rootMetaClass, pmc => isInMetaClassHierarchy(pmc, metaClassName))
        }
    }

    @Native("return anObject % 1 === 0;")
    private def isInteger(anObject: Any): Boolean = false

    @Native("return anObject.length === 1;")
    private def isChar(anObject: Any): Boolean = false

    @Native("return anObject.metaClass_;")
    def getObjectMetaClass(anObject: Any): MetaClass = null

    @Native("""
        for (var i in rootMetaClass.parentClasses) {
            if (predicate(self.getMetaClass(rootMetaClass.parentClasses[i].prototype)) {
                return true;
            }
        }
        return false;
    """)
    private def existsParentMetaClass(rootMetaClass: MetaClass, predicate: MetaClass => Boolean): Boolean = false

    private def asInstanceOf(anObject: Any, className: String): Any = {
        // Just check if the conversion is possible. Nothing has to be done with the object.
        if (!isInstanceOf(anObject, className)) {
            throw new RuntimeException // TODO message and proper type
        }
        anObject
    }
}
