package s2js.runtime.client.scala.math

import s2js.compiler.javascript

object `package`
{
    @javascript("Math.E")
    val E: Double = 0

    @javascript("Math.PI")
    val Pi: Double = 0

    @javascript("return Math.abs(x);")
    def abs(x: AnyVal): Double = 0

    @javascript("return Math.acos(x);")
    def acos(x: AnyVal): Double = 0

    @javascript("return Math.asin(x);")
    def asin(x: AnyVal): Double = 0

    @javascript("return Math.atan(x);")
    def atan(x: AnyVal): Double = 0

    @javascript("return Math.atan2(x);")
    def atan2(x: AnyVal): Double = 0

    @javascript("return Math.ceil(x);")
    def ceil(x: AnyVal): Double = 0

    @javascript("return Math.cos(x);")
    def cos(x: AnyVal): Double = 0

    @javascript("return Math.exp(x);")
    def exp(x: AnyVal): Double = 0

    @javascript("return Math.floor(x);")
    def floor(x: AnyVal): Double = 0

    @javascript("return Math.log(x);")
    def log(x: AnyVal): Double = 0

    @javascript("return Math.max(x, y);")
    def max(x: AnyVal, y: AnyVal): Double = 0

    @javascript("return Math.min(x, y);")
    def min(x: AnyVal, y: AnyVal): Double = 0

    @javascript("return Math.pow(x, y);")
    def pow(x: AnyVal, y: AnyVal): Double = 0

    @javascript("return Math.sqrt(x);")
    def sqrt(x: AnyVal): Double = 0

    @javascript("return Math.random();")
    def random(): Double = 0

    @javascript("return Math.round(x);")
    def round(x: AnyVal): Double = 0

    @javascript("return Math.sin(x);")
    def sin(x: AnyVal): Double = 0

    @javascript("return Math.tan(x);")
    def tan(x: AnyVal): Double = 0

    @javascript("if(x > 0) { return 1; } else if(x == 0) { return 0; } else { return -1; }")
    def signum(x: AnyVal): Int = 0
}
