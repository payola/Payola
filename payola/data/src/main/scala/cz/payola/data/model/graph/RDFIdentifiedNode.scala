package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass
import cz.payola.scala2json.traits.JSONSerializationCustomFields

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.IdentifiedVertex])
class RDFIdentifiedNode(override val uri: String) extends RDFNode with cz.payola.common.rdf.IdentifiedVertex
    with JSONSerializationCustomFields {

    /** @see JSONSerializationCustomFields
      *
      * @return Iterable collection for the field names.
      */
    override def fieldNamesForJSONSerialization(ctx: Any) = List("uri")

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
            case _ => null
        }
    }

}
