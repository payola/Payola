package s2js.runtime.client.rpc

import scala.collection._
import s2js.adapters.js
import s2js.runtime.client.core._
import s2js.runtime.client.js._
import s2js.runtime.shared.rpc.RpcException
import s2js.runtime.shared.DependencyProvider

class Deserializer extends RpcResultTraverser[Any]
{
    private val classNameRetriever = new ClassNameRetriever()

    private var context = new DeserializationContext()

    def deserialize(value: Any): Any = {
        // Retrieve the class names of the classes that has to be loaded while deserializing the object and load them.
        val classesToLoad = classNameRetriever.traverse(value).filter(!classLoader.isLoaded(_))
        if (classesToLoad.nonEmpty) {
            val loadedClasses = mutable.ListBuffer.empty[String]
            new JsArray(classLoader.loadedClasses).foreach((i, c) => loadedClasses += c.toString)
            js.eval(DependencyProvider.get(classesToLoad, loadedClasses).javaScript)
        }

        // Deserialize the object. As a side-effect, the deserialization context is filled.
        context = new DeserializationContext()
        val result = traverse(value)

        // Resolve the references.
        context.resolveReferences()

        result
    }

    protected def nonFutureInstanceVisitor(nonInstance: Any, items: collection.Seq[Any]): Any = {
        val referencedObjectId = JsObject.fromAny(nonInstance).flatMap(_.getInt("__ref__"))
        if (referencedObjectId.isDefined) {
            new Reference(referencedObjectId.get)
        } else if (isArray(nonInstance)) {
            // If the non-instance is an array return traversed items instead of the array with non-traversed items.
            items
        } else {
            nonInstance
        }
    }

    protected def futureInstanceVisitor(jsObject: JsObject, properties: collection.Map[String, Any],
        className: String): Any = {
        val instance = createInstance(className)
        val instanceJsObject = new JsObject(instance)

        // Deserialize the object and register it.
        instance match {
            case _: Seq[_] => deserializeSeq(instanceJsObject, properties)
            case _ => deserializeObject(instanceJsObject, properties, jsObject)
        }
        context.registerInstance(jsObject, instance)

        instance
    }

    private def deserializeSeq(instanceJsObject: JsObject, properties: collection.Map[String, Any]) {
        // Retrieve and set the items.
        val items = properties.get("__value__") match {
            case Some(seq: Seq[_]) => seq
            case _ => Nil
        }
        instanceJsObject.set("internalJsArray", items.toBuffer)

        // Register the references.
        var index = 0
        items.foreach {item =>
            context.addReference(item, instanceJsObject, "internalJsArray[" + index + "]")
            index += 1
        }
    }

    private def deserializeObject(instanceJsObject: JsObject, properties: collection.Map[String, Any],
        jsObject: JsObject) {
        // Set the properties and register the references.
        jsObject.foreachNonInternal {(propertyName, propertyValue) =>
            val traversedPropertyValue = properties(propertyName)
            instanceJsObject.set(propertyName, traversedPropertyValue)
            context.addReference(traversedPropertyValue, instanceJsObject, propertyName)
        }
    }

    private def createInstance(className: String): Any = {
        if (!classLoader.isLoaded(className)) {
            throw new RpcException("Can't deserialize an instance of class " + className + ". The class isn't loaded.")
        }
        js.eval("new " + className + "()")
    }
}
