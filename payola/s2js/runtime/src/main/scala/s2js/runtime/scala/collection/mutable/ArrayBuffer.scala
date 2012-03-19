package s2js.runtime.scala.collection.mutable

import s2js.compiler.javascript

object ArrayBuffer extends s2js.runtime.scala.collection.SeqCompanion
{
    def empty = new ArrayBuffer

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class ArrayBuffer extends s2js.runtime.scala.collection.Seq
{
    def newInstance = ArrayBuffer.empty
}
