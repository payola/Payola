package s2js.runtime.scala.math

import s2js.compiler.NativeJs

object `package` {
    @NativeJs("Math.E")
    val E: Double = 0

    @NativeJs("Math.PI")
    val Pi: Double = 0

    @NativeJs("return Math.abs(x);")
    def abs(x: AnyVal): Double = 0

    @NativeJs("return Math.acos(x);")
    def acos(x: AnyVal): Double = 0

    @NativeJs("return Math.asin(x);")
    def asin(x: AnyVal): Double = 0

    @NativeJs("return Math.atan(x);")
    def atan(x: AnyVal): Double = 0

    @NativeJs("return Math.atan2(x);")
    def atan2(x: AnyVal): Double = 0

    @NativeJs("return Math.ceil(x);")
    def ceil(x: AnyVal): Double = 0

    @NativeJs("return Math.cos(x);")
    def cos(x: AnyVal): Double = 0

    @NativeJs("return Math.exp(x);")
    def exp(x: AnyVal): Double = 0

    @NativeJs("return Math.floor(x);")
    def floor(x: AnyVal): Double = 0

    @NativeJs("return Math.log(x);")
    def log(x: AnyVal): Double = 0

    @NativeJs("return Math.max(x, y);")
    def max(x: AnyVal, y: AnyVal): Double = 0

    @NativeJs("return Math.min(x, y);")
    def min(x: AnyVal, y: AnyVal): Double = 0

    @NativeJs("return Math.pow(x, y);")
    def pow(x: AnyVal, y: AnyVal): Double = 0

    @NativeJs("return Math.random();")
    def random(): Double = 0

    @NativeJs("return Math.round(x);")
    def round(x: AnyVal): Double = 0

    @NativeJs("return Math.sin(x);")
    def sin(x: AnyVal): Double = 0

    @NativeJs("return Math.tan(x);")
    def tan(x: AnyVal): Double = 0
}
