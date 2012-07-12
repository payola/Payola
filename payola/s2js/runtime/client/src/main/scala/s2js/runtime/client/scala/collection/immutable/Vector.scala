package s2js.runtime.client.scala.collection.immutable

import s2js.compiler.javascript

object Vector extends s2js.runtime.client.scala.collection.SeqCompanion[Vector]
{
    def empty = new Vector

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class Vector extends s2js.runtime.client.scala.collection.immutable.Seq
{
    def newInstance = Vector.empty
}
