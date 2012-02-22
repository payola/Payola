package cz.payola.common.rdf

/**
  * An edge between two vertices in a RDF graph.
  */
trait Edge extends IdentifiedObject
{
    val origin: Vertex

    val destination: Vertex
}
