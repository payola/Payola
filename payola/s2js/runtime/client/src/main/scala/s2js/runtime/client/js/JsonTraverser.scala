package s2js.runtime.client.js

import collection._
import s2js.runtime.client.core._

/**
 * A base class for JSON object traversers.
 * @tparam A Type of the traversal result.
 */
abstract class JsonTraverser[A]
{
    /**
     * Traverses the specified object.
     * @param value The object to traverse.
     * @return The result of the traversal.
     */
    def traverse(value: Any): A = {
        if (isArray(value)) {
            // Traverse the items and visit the array.
            val jsArray = new JsArray(value)
            val traversedItems = new mutable.ArrayBuffer[A]()
            jsArray.foreach((index, item) => traversedItems += traverse(item))
            arrayVisitor(jsArray, traversedItems)

        } else if (isObject(value)) {
            // Traverse the properties and visit the object.
            val jsObject = new JsObject(value)
            val traversedProperties = new mutable.HashMap[String, A]()
            if (objectIsTraversable(jsObject)) {
                jsObject.foreach((name, value) => traversedProperties.put(name, traverse(value)))
            }
            objectVisitor(jsObject, traversedProperties)
        } else {
            // Just visit the value.
            valueVisitor(value)
        }
    }

    /**
     * A function that gets invoked when an object is visited.
     * @param jsObject The object that is being visited
     * @param properties A map of the object property traversal results.
     * @return The result of the object traversal.
     */
    protected def objectVisitor(jsObject: JsObject, properties: collection.Map[String, A]): A

    /**
     * A function that gets invoked when an array is visited.
     * @param jsArray The array that is being visited.
     * @param items A sequence of the array item traversal results.
     * @return The result of the array traversal.
     */
    protected def arrayVisitor(jsArray: JsArray, items: collection.Seq[A]): A

    /**
     * A function that gets invoked when a primitive value is visited.
     * @param value The value that is being visited.
     * @return The result of the value traversal.
     */
    protected def valueVisitor(value: Any): A

    /**
     * Returns whether the properties of the specified object should be traversed.
     * @param jsObject The object to check.
     * @return True if the object is traversable, false otherwise.
     */
    protected def objectIsTraversable(jsObject: JsObject): Boolean = true
}
