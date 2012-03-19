package s2js.runtime.scala.collection.immutable

import s2js.compiler.javascript

object StringOps extends s2js.runtime.scala.collection.SeqCompanion
{
    def empty = new StringOps("")

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class StringOps(x: String) extends s2js.runtime.scala.collection.Seq
{
    initializeInternalJsArray(x)

    def newInstance = StringOps.empty

    @javascript("self.setInternalJsArray(value.split(''))")
    def initializeInternalJsArray(value: String) {}

    @javascript("return self.getInternalJsArray().join();")
    def repr: String = ""

    override def toString = mkString("", "", "")
}
