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
      * @param vertexURI URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getOutgoingEdges(vertexURI: String): immutable.Seq[Edge] = {
        edges.filter(_.origin.uri == vertexURI)
    }

    /**
      * Returns all edges that go to the specified vertex (i.e. the vertex is an object in the relation).
      * @param vertexURI URI of the vertex whose edges to retrieve.
      * @return The edges.
      */
    def getIncomingEdges(vertexURI: String): immutable.Seq[Edge] = {
        edges.filter {e=>
            e.destination match {
                case v: IdentifiedVertex => v.uri == vertexURI
                case _ => false
            }
        }
    }
}
