package cz.payola.common.rdf.generic

import cz.payola.common.rdf
import cz.payola.common.rdf.{Vertex}

/**
  * An edge between two vertices in a RDF graph.
  * @tparam A Type of the vertices, the edge is between.
  */
trait Edge[+A <: Vertex] extends rdf.Edge
{
    override val origin: A

    override val destination: A
}
