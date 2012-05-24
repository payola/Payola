package cz.payola.common.rdf

import scala.collection.immutable

/**
  * A RDF graph.
  */
trait Graph
{
    /** Type of the edges, that are between the vertices. */
    type EdgeType <: Edge

    protected val _vertices: collection.Seq[EdgeType#VertexType]

    protected val _edges: collection.Seq[EdgeType]

    def vertices: collection.Seq[EdgeType#VertexType] = _vertices

    def edges: collection.Seq[EdgeType] = _edges

    /**
      * Returns whether the graph is empty (i.e. contains no vertices).
      */
    def isEmpty: Boolean = vertices.isEmpty

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
        edges.filter {e =>
            e.destination match {
                case v: IdentifiedVertex => v.uri == vertexURI
                case _ => false
            }
        }
    }
}
