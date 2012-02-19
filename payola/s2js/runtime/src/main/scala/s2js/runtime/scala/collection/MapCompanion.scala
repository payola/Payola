package s2js.runtime.scala.collection

import s2js.compiler.NativeJs

trait MapCompanion
{
    def empty: Map[Any, Any]

    def apply(xs: Any*): Map[Any, Any] = {
        var m = empty
        xs.foreach(m += _)
        m
    }
}
