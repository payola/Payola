package cz.payola.data.model.graph

import cz.payola.scala2json.traits.JSONSerializationFullyCustomized
import collection.mutable.HashMap
import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.annotations.{JSONTransient, JSONPoseableClass}

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.generic.Edge])
class RDFEdge(override val origin: RDFIdentifiedNode, override val destination: RDFNode, val uri: String)
    extends cz.payola.common.rdf.generic.Edge with JSONSerializationFullyCustomized {

    type VertexType = RDFNode
    type IdentifiedVertexType = RDFIdentifiedNode

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

        hash.put("uri", graph.shortenedNamespace(uri))
        if (origin != null) {
            hash.put("origin", origin.objectID)
        }else{
            hash.put("origin", Option.empty[Any])
        }

        if (destination != null){
            hash.put("destination", destination.objectID)
        }else{
            hash.put("destination", Option.empty[Any])
        }

        val serializer = new JSONSerializer(hash, options)
        serializer.context = ctx
        serializer.stringValue
    }

}
