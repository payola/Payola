package s2js.runtime.scala.collection.mutable

import s2js.compiler.NativeJs

object ListBuffer {
    @NativeJs("""
        var a = new scala.collection.mutable.ListBuffer();
        a.internalArray = nativeArray;
        return a;
    """)
    def fromNative(nativeArray: AnyRef): ListBuffer = null

    @NativeJs("return self.fromNative(xs.internalArray);")
    def apply(xs: Any*): ListBuffer = null
}

class ListBuffer {
    @NativeJs("[]")
    var internalArray = null

    @NativeJs("for (var i in self.internalArray) f(self.internalArray[i]);")
    def foreach(f: Any => Unit) {}

    def exists(p: Any => Boolean): Boolean = {
        find(p).isDefined
    }

    def find(p: Any => Boolean): Option[Any] = {
        Option(nativeFind(p))
    }

    @NativeJs("self.internalArray.push(value);")
    def +=(value: Any) { }

    @NativeJs("""
        var index = internalArray.indexOf(value);
        if (index != -1) {
            internalArray.splice(index, 1);
        }
    """)
    def -=(value: Any) { }

    @NativeJs("""
        for (var i in self.internalArray) {
            if (p(self.internalArray[i])) {
                return self.internalArray[i];
            }
        }
        return null;
    """)
    private def nativeFind(p: Any => Boolean): Any = null
}
