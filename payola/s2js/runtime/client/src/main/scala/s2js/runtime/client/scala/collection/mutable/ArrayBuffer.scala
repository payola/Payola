package s2js.runtime.client.scala.collection.mutable

import s2js.compiler.javascript

object ArrayBuffer extends s2js.runtime.client.scala.collection.SeqCompanion[ArrayBuffer]
{
    def empty = new ArrayBuffer

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class ArrayBuffer extends s2js.runtime.client.scala.collection.Seq
{
    def newInstance = ArrayBuffer.empty

    def map(f: Any => Any): ArrayBuffer = {
        val b = newInstance
        for (x <- this) {
            b += f(x)
        }
        b
    }
}
