package s2js.runtime.scala.collection.immutable

import s2js.compiler.NativeJs

object List extends s2js.runtime.scala.collection.SeqCompanion
{
    def empty = new List

    @NativeJs("return self.fromJsArray(xs.internalJsArray);")
    def apply(xs: Any*): s2js.runtime.scala.collection.Seq = null
}

class List extends s2js.runtime.scala.collection.Seq
{
    def newInstance = List.empty
}

object Nil extends List
{
    override def isEmpty = true

    override def head: Nothing = throw new s2js.runtime.scala.NoSuchElementException("head of empty list")

    override def tail: List = throw new s2js.runtime.scala.UnsupportedOperationException("tail of empty list")
}
