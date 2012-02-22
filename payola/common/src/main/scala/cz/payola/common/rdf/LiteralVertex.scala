package cz.payola.common.rdf

/**
  * A literal vertex in a RDF graph.
  */
trait LiteralVertex extends Vertex
{
    /** Value of the literal vertex. */
    val value: String

    /** Optional language of the literal. */
    val language: Option[String]

    /** Optional xsd data type URI of the literal. */
    val typeUri: Option[String]
}
