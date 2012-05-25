package cz.payola.domain.rdf

/**
  * A node in the RDF graph which only contains a literal value.
  * @param _value Value of the node.
  * @param _language Language of the node.
  */
class LiteralNode(protected val _value: Any, protected val _language: Option[String] = None)
    extends Node with cz.payola.common.rdf.LiteralVertex {

    /** Equality based on the value and language.
      *
      * @param other Other object.
      * @return True or false.
      */
    override def equals(other: Any): Boolean = {
        other match {
            case ln: LiteralNode => this.value == ln.value && this.language == ln.language
            case _ => false
        }
    }

    /** Hash code of the node.
      *
      * @return Hash code.
      */
    override def hashCode: Int = {
        this.value.hashCode ^ this.language.hashCode
    }

}
