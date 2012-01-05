package s2js.runtime.scala.collection.immutable

import s2js.compiler.NativeJs

object List {
    @NativeJs("""
        var a = new scala.collection.immutable.List();
        a.internalArray = nativeArray;
        return a;
    """)
    def fromNative(nativeArray: AnyRef): List = null

    @NativeJs("return self.fromNative(xs.internalArray);")
    def apply(xs: Any*): List = null
}

class List {
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
