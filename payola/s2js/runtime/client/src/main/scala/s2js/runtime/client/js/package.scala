package s2js.runtime.client.js

import s2js.compiler.{dependency, javascript}

@dependency("goog")
object `package`
{
    def isDefined(anObject: Any): Boolean = !isUndefined(anObject)

    @javascript("return goog.typeOf(anObject) === 'undefined';")
    def isUndefined(anObject: Any): Boolean = false

    @javascript("return goog.typeOf(anObject) === 'object';")
    def isObject(anObject: Any): Boolean = false

    @javascript("return goog.typeOf(anObject) === 'array';")
    def isArray(anObject: Any): Boolean = false

    @javascript("return anObject % 1 === 0;")
    def isInteger(anObject: Any): Boolean = false

    @javascript("return anObject.length === 1;")
    def isChar(anObject: Any): Boolean = false
}
