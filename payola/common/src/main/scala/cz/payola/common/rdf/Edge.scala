package cz.payola.common.rdf

object Edge
{
    val rdfTypeEdge = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"

    val rdfLabelEdges = List(
        "http://www.w3.org/2000/01/rdf-schema#label",
        "http://purl.org/dc/terms/title"
    )
}

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
