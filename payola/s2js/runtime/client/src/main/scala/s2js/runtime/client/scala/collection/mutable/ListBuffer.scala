package s2js.runtime.client.scala.collection.mutable

import s2js.compiler.javascript

object ListBuffer extends s2js.runtime.client.scala.collection.SeqCompanion[ListBuffer]
{
    def empty = new ListBuffer

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class ListBuffer extends s2js.runtime.client.scala.collection.Seq
{
    def newInstance = ListBuffer.empty
}
