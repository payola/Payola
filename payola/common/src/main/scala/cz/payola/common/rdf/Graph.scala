package cz.payola.common.rdf

/**
  * A RDF graph.
  */
trait Graph
{
    /** Type of the edges, that are between the vertices. */
    type EdgeType <: Edge

    val vertices: Seq[EdgeType#VertexType]

    val edges: Seq[EdgeType]

    /**
      * Returns all edges that go from the specified vertex (i.e. the vertex is a subject in the relation).
      * @param vertexURI URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getOutgoingEdges(vertexURI: String): Seq[Edge] = {
        edges.filter(_.origin.uri == vertexURI)
    }

    /**
      * Returns all edges that go to the specified vertex (i.e. the vertex is an object in the relation).
      * @param vertexURI URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getIncomingEdges(vertexURI: String): Seq[Edge] = {
        edges.filter {e=>
            e.destination match {
                case v: IdentifiedVertex => v.uri == vertexURI
                case _ => false
            }
        }
    }
}
