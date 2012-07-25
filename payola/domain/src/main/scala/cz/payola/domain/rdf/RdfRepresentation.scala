package cz.payola.domain.rdf

/**
  * Representation of a RDF data.
  */
object RdfRepresentation extends Enumeration
{
    type Type = Value

    val RdfXml, Turtle, Trig = Value
}
