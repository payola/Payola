package s2js.runtime.client.rpc

import s2js.runtime.client.js.{JsArray, JsObject, JsonTraverser}

/**
 * Traverser of the RPC JSON results.
 * @tparam A Type of the traversal result.
 */
abstract class RpcResultTraverser[A] extends JsonTraverser[A]
{
    /**
     * A function that gets invoked when an object whose instance doesn't have to be created is visited. That is one
     * of those cases:
     * $ - a primitive value
     * $ - an object without class name
     * $ - a valid instance whose class references an instance of the Class class.
     * @param nonInstance The non-instance that is being visited.
     * @param items A sequence of the nested object traversal results.
     * @return The result of the non-instance traversal.
     */
    protected def nonFutureInstanceVisitor(nonInstance: Any, items: collection.Seq[A]): A

    /**
     * A function that gets invoked when an object that has class name specified is visited.
     * @param jsObject The instance that is being visited.
     * @param properties A map of the object property traversal results.
     * @param className The instance fully qualified class name.
     * @return The result of the object traversal.
     */
    protected def futureInstanceVisitor(jsObject: JsObject, properties: collection.Map[String, A], className: String): A

    protected def objectVisitor(jsObject: JsObject, properties: collection.Map[String, A]): A = {
        getObjectClass(jsObject) match {
            case Some(className: String) => futureInstanceVisitor(jsObject, properties, className)
            case _ => nonFutureInstanceVisitor(jsObject.wrappedObject, Nil)
        }
    }

    protected def arrayVisitor(jsArray: JsArray, items: collection.Seq[A]): A = {
        nonFutureInstanceVisitor(jsArray.wrappedArray, items)
    }

    protected def valueVisitor(value: Any): A = {
        nonFutureInstanceVisitor(value, Nil)
    }

    override protected def objectIsTraversable(jsObject: JsObject): Boolean = {
        // Only objects that have properly specified class should be traversed.
        getObjectClass(jsObject) match {
            case Some(_: String) => true
            case _ => false
        }
    }

    private def getObjectClass(jsObject: JsObject): Option[Any] = {
        jsObject.get("__class__").orElse(jsObject.get("__arrayClass__"))
    }
}
