package cz.payola.common.rdf

/**
  * A literal vertex in a RDF graph.
  */
trait LiteralVertex extends Vertex
{
    /** Value of the literal vertex. */
    val value: Any

    /** Optional language of the literal. */
    val language: Option[String]

}
