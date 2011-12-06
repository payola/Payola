package s2js.runtime.scala

import s2js.compiler.Native

object Array {
    @Native("""
        var a = new scala.Array(jsArray.length);
        a.internalArray = jsArray;
        return a;
    """)
    def fromNative(jsArray: AnyRef): Array = null

    def apply(xs: Any*): Array = {
        val array = new Array(xs.length)
        var i = 0
        for (x <- xs.iterator) {
            array(i) = x;
            i += 1
        }
        array
    }
}

class Array(val length: Int) {
    @Native("[]")
    var internalArray = null

    @Native("return self.internalArray[i];")
    def apply(i: Int): Any = null

    @Native("return self.internalArray.slice(0);")
    override def clone(): Array = null

    @Native("self.internalArray[i] = x;")
    def update(i: Int, x: Any): Unit = ()
}
