package s2js.runtime.scala

import s2js.compiler.NativeJs

object Int {
    @NativeJs("Math.pow(2, 53)")
    final val MinValue = 0

    @NativeJs("-Math.pow(2, 53)")
    final val MaxValue = 1

    override def toString = "object scala.Int"
}
