package s2js.runtime.client.js

import s2js.compiler.javascript
import s2js.runtime.client.core._

object JsObject
{
    def fromAny(x: Any): Option[JsObject] = if (isObject(x)) Some(new JsObject(x)) else None

    @javascript("return new s2js.runtime.client.js.JsObject({});")
    def empty: JsObject = null
}

class JsObject(val wrappedObject: Any)
{
    if (!isObject(wrappedObject)) {
        throw new RuntimeException("A non-object can't be wrapped with s2js.runtime.client.js.JsObject.")
    }

    def get(propertyName: String): Option[Any] = {
        internalGet(propertyName) match {
            case value if isDefined(value) => Some(value)
            case _ => None
        }
    }

    def getString(propertyName: String): Option[String] = {
        get(propertyName).flatMap {
            case s: String => Some(s)
            case _ => None
        }
    }

    def getInt(propertyName: String): Option[Int] = {
        get(propertyName).flatMap {
            case s: Int => Some(s)
            case _ => None
        }
    }

    def getJsArray(propertyName: String): Option[JsArray] = {
        get(propertyName).flatMap(JsArray.fromAny(_))
    }

    @javascript("self.wrappedObject[propertyName] = value;")
    def set(propertyName: String, value: Any) {}

    @javascript("""
        for (var i in self.wrappedObject) {
            f(i, self.wrappedObject[i]);
        }
    """)
    def foreach(f: ((String, Any) => Unit)) {}

    def foreachNonInternal(f: ((String, Any) => Unit)) {
        foreach {(name, value) =>
            if (!name.startsWith("__")) {
                f(name, value)
            }
        }
    }

    @javascript("return self.wrappedObject[propertyName];")
    private def internalGet(propertyName: String): Any = null
}
