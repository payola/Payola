package s2js.runtime.client.scala

import s2js.compiler.javascript
import s2js.runtime.client.scala.collection.immutable.StringOps

class String
{
    @javascript("return self.length;")
    def length = 0

    @javascript("return self.indexOf(c);")
    def indexOf(c: Char): Int = -1

    @javascript("return self.lastIndexOf(c);")
    def lastIndexOf(c: Char): Int = -1

    def startsWith(that: String): Boolean = asStringOps.startsWith(that.asStringOps)

    def endsWith(that: String): Boolean = asStringOps.endsWith(that.asStringOps)

    @javascript("return new scala.collection.immutable.StringOps(self);")
    def asStringOps: StringOps = null
}
