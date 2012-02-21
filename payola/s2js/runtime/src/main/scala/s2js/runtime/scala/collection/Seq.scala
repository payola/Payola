package s2js.runtime.scala.collection

import s2js.compiler.NativeJs

trait Seq extends Iterable
{
    @NativeJs("[]")
    var internalJsArray = null

    @NativeJs("""
        for (var i in self.internalJsArray) {
            f(self.internalJsArray[i]);
        }
    """)
    def foreach[U](f: Double => U) {}

    @NativeJs("self.internalJsArray.push(x);")
    def +=(x: Any) {}

    // From TraversableLike
    def reversed: Iterable = {
        val elems: Iterable = newInstance
        for (x <- this) {
            elems.prepend(x)
        }
        elems
    }

    @NativeJs("return self.internalJsArray.length;")
    override def size: Int = 0

    @NativeJs("""
        if (s2js.isUndefined(self.internalJsArray[n])) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        return self.internalJsArray[n];
    """)
    def apply(n: Int): Any = null

    @NativeJs("""
        if (self.size() <= n) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        self.internalJsArray[n] = newelem;
    """)
    def update(n: Int, newelem: Any) {}

    def length: Int = size

    @NativeJs("""
        if (index < 0 || self.size() <= index) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        self.internalJsArray.splice(index, 1);
    """)
    def remove(index: Int) {}

    @NativeJs("""self.internalJsArray.splice(0, 0, x);""")
    def prepend(x: Any) {}

    @NativeJs("""
        var index = self.internalJsArray.indexOf(x);
        if (index != -1) {
            self.internalJsArray.splice(index, 1);
        }
    """)
    def -=(x: Double) {}
}


