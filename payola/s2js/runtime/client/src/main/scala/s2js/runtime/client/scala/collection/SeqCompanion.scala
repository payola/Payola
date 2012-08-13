package s2js.runtime.client.scala.collection

import s2js.compiler.javascript

trait SeqCompanion[A >: Null <: Seq]
{
    def empty: Any

    @javascript("""
        var a = self.empty();
        a.setInternalJsArray(jsArray);
        return a;
    """)
    def fromJsArray(jsArray: Any): A = null

    // Just a hack to make the map function work.
    def canBuildFrom: Boolean = true
}
