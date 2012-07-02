package s2js.runtime.client.scala.collection.immutable

import s2js.compiler.javascript

object List extends s2js.runtime.client.scala.collection.SeqCompanion[List]
{
    def empty = new List

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class List extends s2js.runtime.client.scala.collection.immutable.Seq
{
    def newInstance = List.empty
}

object Nil extends List
{
    override def isEmpty = true

    override def head: Nothing = throw new s2js.runtime.client.scala.NoSuchElementException("head of empty list")

    override def tail: List = throw new s2js.runtime.client.scala.UnsupportedOperationException("tail of empty list")
}
