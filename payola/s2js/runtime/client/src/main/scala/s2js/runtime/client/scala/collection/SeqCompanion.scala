package s2js.runtime.client.scala.collection

import s2js.compiler.javascript

trait SeqCompanion
{
    def empty: Seq

    @javascript("""
        var a = self.empty();
        a.setInternalJsArray(jsArray);
        return a;
    """)
    def fromJsArray(jsArray: Any): Seq = null
}
