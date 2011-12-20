package s2js.runtime.scala

import s2js.compiler.NativeJs

object Array {
    @NativeJs("""
        var a = new scala.Array(nativeArray.length);
        a.internalArray = nativeArray;
        return a;
    """)
    def fromNative(nativeArray: AnyRef): Array = null

    @NativeJs("return self.fromNative(xs.internalArray);")
    def apply(xs: Any*): Array = null
}

class Array(val length: Int) {
    @NativeJs("[]")
    var internalArray = null

    @NativeJs("return self.internalArray[i];")
    def apply(i: Int): Any = null

    @NativeJs("return self.internalArray.slice(0);")
    override def clone(): Array = null

    @NativeJs("self.internalArray[i] = x;")
    def update(i: Int, x: Any) {}

    @NativeJs("""self.internalArray.forEach(f);""")
    def foreach(f: Any => Unit) {}
}
