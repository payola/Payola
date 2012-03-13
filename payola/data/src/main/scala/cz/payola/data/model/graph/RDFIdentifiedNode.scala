package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass
import cz.payola.scala2json.traits.JSONSerializationFullyCustomized
import collection.mutable.HashMap
import cz.payola.scala2json.JSONSerializer
import cz.payola.common.rdf.IdentifiedVertex

@JSONPoseableClass(otherClass = classOf[IdentifiedVertex])
class RDFIdentifiedNode(val uri: String) extends RDFNode with IdentifiedVertex with JSONSerializationFullyCustomized
{
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
        hash.put("uri", graph.shortenedNamespace(uri))

        val serializer = new JSONSerializer(hash, options)
        serializer.context = ctx
        serializer.stringValue
    }

}
