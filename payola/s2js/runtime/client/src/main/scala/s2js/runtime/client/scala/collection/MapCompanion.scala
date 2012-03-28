package s2js.runtime.client.scala.collection

trait MapCompanion
{
    def empty: Map[Any, Any]

    def apply(xs: Any*): Map[Any, Any] = {
        var m = empty
        xs.foreach(m += _)
        m
    }
}
