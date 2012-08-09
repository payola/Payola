package s2js.runtime.client.js

import s2js.compiler.javascript
import s2js.runtime.client.core._

object JsArray
{
    def fromAny(x: Any): Option[JsArray] = if (isArray(x)) Some(new JsArray(x)) else None

    @javascript("return new s2js.runtime.client.js.JsArray([]);")
    def empty: JsArray = null
}

class JsArray(val wrappedArray: Any)
{
    if (!isArray(wrappedArray)) {
        throw new RuntimeException("A non-array can't be wrapped with s2js.runtime.client.js.JsArray.")
    }

    def get(index: Int): Option[Any] = {
        internalGet(index) match {
            case value if isDefined(value) => Some(value)
            case _ => None
        }
    }

    @javascript("self.wrappedArray[index] = value;")
    def set(index: Int, value: Any) {}

    @javascript("""
        for (var i in self.wrappedArray) {
            f(i, self.wrappedArray[i]);
        }
    """)
    def foreach(f: ((Int, Any) => Unit)) {}

    @javascript("return self.wrappedArray[index];")
    private def internalGet(index: Int): Any = null
}
