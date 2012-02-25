package cz.payola.common.rdf.generic

import scala.collection.immutable
import cz.payola.common.rdf
import rdf.{IdentifiedVertex, Vertex}

/**
  * A RDF graph.
  */
trait Graph extends rdf.Graph
{
    /** Type of the edges, that are between the vertices. */
    type EdgeType <: Edge

    val vertices: immutable.Seq[EdgeType#VertexType]

    val edges: immutable.Seq[EdgeType]
}
