package s2js.runtime.client.rpc

import s2js.runtime.client.js.JsObject
import scala.collection.mutable.ArrayBuffer

class ClassNameRetriever extends RpcResultTraverser[Seq[String]]
{
    protected def nonFutureInstanceVisitor(nonInstance: Any, items: collection.Seq[Seq[String]]): Seq[String] = {
        items.flatMap((item: Seq[String]) => item)
    }

    protected def futureInstanceVisitor(jsObject: JsObject, properties: collection.Map[String, Seq[String]],
        className: String): Seq[String] = {
        val result = ArrayBuffer[String](className)
        properties.foreach(pair => result ++= pair._2)
        result
    }
}
