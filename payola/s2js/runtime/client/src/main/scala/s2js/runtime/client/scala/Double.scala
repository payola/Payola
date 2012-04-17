package s2js.runtime.client.scala

import s2js.compiler.javascript

object Double
{
    @javascript("Number.MIN_VALUE")
    final val MinPositiveValue = java.lang.Double.MIN_VALUE

    @javascript("0 / 0")
    final val NaN = java.lang.Double.NaN

    @javascript("Number.POSITIVE_INFINITY")
    final val PositiveInfinity = java.lang.Double.POSITIVE_INFINITY

    @javascript("Number.NEGATIVE_INFINITY")
    final val NegativeInfinity = java.lang.Double.NEGATIVE_INFINITY

    @javascript("- Number.MAX_VALUE")
    final val MinValue = 0

    @javascript("Number.MAX_VALUE")
    final val MaxValue = 0
}

