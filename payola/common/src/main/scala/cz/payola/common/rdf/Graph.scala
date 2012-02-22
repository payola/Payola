package cz.payola.common.rdf

import scala.collection.immutable

/**
  * A RDF graph.
  */
trait Graph
{
    val vertices: immutable.Seq[Vertex]

    val edges: immutable.Seq[Edge]

    /**
      * Returns all edges that go from the specified vertex (i.e. the vertex is a subject in the relation).
      * @param vertexUri URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getOutgoingEdges(vertexUri: String): immutable.Seq[Edge] = {
        edges.filter(_.origin.uri == vertexUri)
    }

    /**
      * Returns all edges that go to the specified vertex (i.e. the vertex is an object in the relation).
      * @param vertexUri URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getIncomingEdges(vertexUri: String): immutable.Seq[Edge] = {
        edges.filter(_.destination.uri == vertexUri)
    }
}
