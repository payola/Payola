package cz.payola.common.rdf.generic

import scala.collection.immutable
import cz.payola.common.rdf
import cz.payola.common.rdf.Vertex

/**
  * A RDF graph.
  * @tparam A Type of the vertices, the graph consists of.
  * @tparam B Type of the edges, that are between the vertices.
  */
trait Graph[+A <: Vertex, +B <: Edge[A]] extends rdf.Graph
{
    override val vertices: immutable.Seq[A]

    override val edges: immutable.Seq[B]
}
