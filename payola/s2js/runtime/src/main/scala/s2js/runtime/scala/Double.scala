package s2js.runtime.scala

import s2js.compiler.NativeJs

object Double
{
    @NativeJs("Number.MIN_VALUE")
    final val MinPositiveValue = java.lang.Double.MIN_VALUE

    @NativeJs("0 / 0")
    final val NaN = java.lang.Double.NaN

    @NativeJs("Number.POSITIVE_INFINITY")
    final val PositiveInfinity = java.lang.Double.POSITIVE_INFINITY

    @NativeJs("Number.NEGATIVE_INFINITY")
    final val NegativeInfinity = java.lang.Double.NEGATIVE_INFINITY

    @NativeJs("- Number.MAX_VALUE")
    final val MinValue = 0

    @NativeJs("Number.MAX_VALUE")
    final val MaxValue = 0
}

