package cz.payola.common.rdf

/**
  * A literal vertex in a RDF graph.
  */
trait LiteralVertex extends Vertex
{
    protected val _value: Any

    protected val _language: Option[String]

    /** Value of the literal vertex. */
    def value = _value

    /** Optional language of the literal. */
    def language = _language

    /**
      * @return value.toString
      */
    override def toString = value.toString
}
