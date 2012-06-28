package cz.payola.common.rdf

/**
  * A vertex in the RDF graph which only contains a literal value.
  * @param value Value of the vertex.
  * @param language Language of the vertex.
  */
class LiteralVertex(val value: Any, val language: Option[String] = None) extends Vertex
{
    override def toString = value.toString

    override def equals(other: Any): Boolean = {
        other match {
            case lv: LiteralVertex => value == lv.value && language == lv.language
            case _ => false
        }
    }

    override def hashCode: Int = {
        41 * (
            41 + value.hashCode
        ) + language.hashCode
    }
}
