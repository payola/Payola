package cz.payola.common.rdf

import scala.collection.immutable

/**
  * A RDF graph.
  * @param vertices Vertices of the graph.
  * @param edges Edges connecting vertices of the graph.
  */
class Graph(val vertices: immutable.Seq[Vertex], val edges: immutable.Seq[Edge], protected var _resultsCount: Option[Long])
{

    def resultsCount = _resultsCount

    def resultsCount_=(value: Option[Long]) = {
        _resultsCount = value
    }

    /**
      * Returns whether the graph is empty (i.e. contains no vertices).
      */
    def isEmpty: Boolean = vertices.isEmpty

    /**
      * Returns a vertex with the specified URI.
      * @param vertexURI URI of the vertex.
      * @return The vertex or [[scala.None]] if there isn't one with such URI.
      */
    def getVertexWithURI(vertexURI: String): Option[IdentifiedVertex] = {
        vertices.collect {
            case iv: IdentifiedVertex if iv.uri == vertexURI => iv
        }.headOption
    }

    /**
      * Returns whether this graph contains a vertex with the specified URI.
      * @param vertexURI URI of the vertex.
      */
    def containsVertexWithURI(vertexURI: String): Boolean = {
        getVertexWithURI(vertexURI).isDefined
    }

    /**
      * Returns a literal vertex with the specified properties.
      * @param value Value of the vertex.
      * @param language Language of the vertex.
      * @return The vertex or [[scala.None]] if there isn't one with such properties.
      */
    def getVertexWithValue(value: Any, language: Option[String] = None): Option[LiteralVertex] = {
        vertices.collect {
            case lv: LiteralVertex if lv.value == value && lv.language == language => lv
        }.headOption
    }

    /**
      * Returns whether the graph contains a literal vertex with the specified properties.
      * @param value Value of the literal vertex.
      * @param language Language of the literal vertex.
      */
    def containsVertexWithValue(value: Any, language: Option[String] = None): Boolean = {
        getVertexWithValue(value, language).isDefined
    }

    /**
      * Returns an edge with the specified properties.
      * @param edgeOrigin Origin of the edge.
      * @param edgeDestination Destination of the edge.
      * @param edgeURI URI of the edge.
      * @return The edge or [[scala.None]] if there isn't one with such properties.
      */
    def getEdge(edgeOrigin: IdentifiedVertex, edgeDestination: Vertex, edgeURI: String): Option[Edge] = {
        edges.find(e => e.origin == edgeOrigin && e.destination == edgeDestination && e.uri == edgeURI)
    }

    /**
      * Returns whether the graph contains an edge with the specified properties.
      * @param edgeOrigin Origin of the edge.
      * @param edgeDestination Destination of the edge.
      * @param edgeURI URI of the edge.
      */
    def containsEdge(edgeOrigin: IdentifiedVertex, edgeDestination: Vertex, edgeURI: String): Boolean = {
        getEdge(edgeOrigin, edgeDestination, edgeURI).isDefined
    }

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
        edges.filter { e =>
            e.destination match {
                case v: IdentifiedVertex => v.uri == vertexURI
                case _ => false
            }
        }
    }

    def getIncomingEdges(vertex: Vertex): immutable.Seq[Edge] = {
        edges.filter(_.destination == vertex)
    }

    def getOutgoingEdges(vertex: Vertex): immutable.Seq[Edge] = {
        edges.filter(_.origin == vertex)
    }
}
