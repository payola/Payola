package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass
import cz.payola.scala2json.traits.JSONSerializationCustomFields

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.generic.Edge])
class RDFEdge(override val origin: RDFIdentifiedNode, override val destination: RDFNode, val uri: String)
    extends cz.payola.common.rdf.generic.Edge with JSONSerializationCustomFields {

    type VertexType = RDFNode
    type IdentifiedVertexType = RDFIdentifiedNode


    /** @see JSONSerializationCustomFields
      *
      * @return Iterable collection for the field names.
      */
    override def fieldNamesForJSONSerialization(ctx: Any) = List("origin", "destination", "uri")

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    def fieldValueForKey(ctx: Any, key: String): Any = {
        key match {
            case "uri" => {
                require(ctx.isInstanceOf[RDFGraph], "Context isn't an RDFGraph (in RDFIdentifiedNode")
                val graph = ctx.asInstanceOf[RDFGraph]
                graph.shortenedNamespace(uri)
            }
            case "origin" => origin
            case "destination" => destination
            case _ => null
        }
    }


}
