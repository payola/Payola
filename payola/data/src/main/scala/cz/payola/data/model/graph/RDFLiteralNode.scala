package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass
import cz.payola.scala2json.traits.JSONSerializationFullyCustomized
import collection.mutable.HashMap
import cz.payola.scala2json.JSONSerializer

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.LiteralVertex])
class RDFLiteralNode(override val value: Any, override val language: Option[String] = Option.empty[String])
    extends RDFNode with cz.payola.common.rdf.LiteralVertex with JSONSerializationFullyCustomized {

    /** Should return JSON representation of the object.
      *
      * @param options Options for the serialization. @see JSONSerializerOptions
      *
      * @return JSON representation of the object.
      */
    def JSONValue(ctx: Any, options: Int): String = {
        require(ctx.isInstanceOf[RDFGraph], "Context not RDFGraph instance - " + ctx)
        val graph = ctx.asInstanceOf[RDFGraph]
        val hash: HashMap[String, Any] = new HashMap[String, Any]()

        hash.put("__objectID__", objectID)
        hash.put("value", value)
        hash.put("language", language)

        val serializer = new JSONSerializer(hash, options)
        serializer.context = ctx
        serializer.stringValue
    }
}
