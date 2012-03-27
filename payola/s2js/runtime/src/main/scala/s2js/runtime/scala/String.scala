package s2js.runtime.scala

import s2js.compiler.{dependency, javascript}

class String
{
    @javascript("return self.length;")
    def length = 0


    @javascript("return self.indexOf(c);")
    def indexOf(c: Char): Int = 0

    @dependency("scala.collection.immutable.StringOps")
    @javascript("""
        var o = new scala.collection.immutable.StringOps(self);
        return o.endsWith(new scala.collection.immutable.StringOps(that));
    """)
    def endsWith(that: String): Boolean = false
}
