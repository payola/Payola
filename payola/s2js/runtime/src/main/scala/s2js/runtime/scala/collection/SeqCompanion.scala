package s2js.runtime.scala.collection

import s2js.compiler.NativeJs

trait SeqCompanion
{
    def empty: Seq

    @NativeJs("""
        var a = self.empty();
        a.internalJsArray = jsArray;
        return a;
    """)
    def fromJsArray(jsArray: Any): Seq = null
}
