package s2js.runtime.client.rpc

import scala.collection.mutable
import s2js.runtime.client.js.JsObject

class DeserializationContext
{
    val objectRegistry = new mutable.HashMap[Int, Any]()

    val references = new mutable.ArrayBuffer[ReferenceToResolve]()

    def resolveReferences() {
        references.foreach(_.resolve(this))
    }

    def addReference(reference: Any, instanceJsObject: JsObject, propertyName: String) {
        // Add it only if it's a Reference instance.
        reference match {
            case r: Reference => references += new ReferenceToResolve(r, instanceJsObject.wrappedObject, propertyName)
            case _ =>
        }
    }

    def registerInstance(jsObject: JsObject, instance: Any) {
        jsObject.getInt("__objectID__").foreach(id => objectRegistry.put(id, instance))
    }
}
