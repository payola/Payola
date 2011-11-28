package scala

import s2js.compiler.Native

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