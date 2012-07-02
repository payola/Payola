package cz.payola.common.rdf

/**
  * An edge between two vertices in a RDF graph. The origin must be an identified vertex.
  * @param origin Origin of the edge.
  * @param destination Destination of the edge.
  * @param uri URI of the edge.
  */
class Edge(val origin: IdentifiedVertex, val destination: Vertex, val uri: String) extends IdentifiedObject
{
    override def equals(other: Any): Boolean = {
        other match {
            case e: Edge => uri == e.uri && origin == e.origin && destination == e.destination
            case _ => false
        }
    }

    override def hashCode: Int = {
        41 * (
            41 * (
                41 + uri.hashCode
            ) + origin.hashCode
        ) + destination.hashCode
    }
}
