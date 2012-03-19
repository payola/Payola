package s2js.runtime.scala.collection.mutable

import s2js.compiler.javascript

object ListBuffer extends s2js.runtime.scala.collection.SeqCompanion
{
    def empty = new ListBuffer

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class ListBuffer extends s2js.runtime.scala.collection.Seq
{
    def newInstance = ListBuffer.empty
}
