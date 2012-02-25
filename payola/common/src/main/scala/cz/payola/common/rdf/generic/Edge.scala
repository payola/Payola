package cz.payola.common.rdf.generic

import cz.payola.common.rdf
import rdf.{IdentifiedVertex, Vertex}

/**
  * An edge between two vertices in a RDF graph.
  */
trait Edge extends rdf.Edge
{
    /** Type of the vertices, the edge is between. */
    type VertexType <: Vertex

    /** Type of the vertices, the edge can originate in. */
    type IdentifiedVertexType <: IdentifiedVertex

    val origin: IdentifiedVertexType

    val destination: VertexType
}
