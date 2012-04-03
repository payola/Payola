package cz.payola.common.rdf

/**
  * An edge between two vertices in a RDF graph.
  */
trait Edge extends IdentifiedObject
{
    /** Type of the vertices, the edge is between. */
    type VertexType <: Vertex

    /** Type of the vertices, the edge can originate in. */
    type IdentifiedVertexType <: IdentifiedVertex

    protected val _origin: IdentifiedVertexType

    protected val _destination: VertexType

    def origin = _origin

    def destination = _destination
}
