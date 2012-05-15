package cz.payola.domain.rdf

/**
  * A node in the RDF graph identified by the URI.
  * @param _uri URI of the node.
  */
class IdentifiedNode(protected val _uri: String) extends Node with cz.payola.common.rdf.IdentifiedVertex {

    /** Equality defined by URI equality.
      *
      * @param other Other object.
      * @return True or false.
      */
    override def equals(other: Any): Boolean = {
        other match {
            case in: IdentifiedNode => this.uri == in.uri
            case _ => false
        }
    }

    /** Hash code of the node.
      *
      * @return Hash code.
      */
    override def hashCode: Int = {
        this.uri.hashCode
    }
}
