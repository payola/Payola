package cz.payola.common.rdf

/**
  * A vertex in the RDF graph identified by the URI.
  * @param uri URI of the vertex.
  */
class IdentifiedVertex(val uri: String) extends Vertex with IdentifiedObject
{
    override def equals(other: Any): Boolean = {
        other match {
            case iv: IdentifiedVertex => uri == iv.uri
            case _ => false
        }
    }

    override def toString = uri

    override def hashCode: Int = {
        uri.hashCode
    }
}
