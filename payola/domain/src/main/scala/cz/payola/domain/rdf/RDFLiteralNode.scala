package cz.payola.domain.rdf

import cz.payola.common.rdf.LiteralVertex

/** This class represents a node in the RDF graph which only contains a value (i.e.
  * doesn't have a URI).
  *
  * @param _value Value of the node.
  * @param _language Language of the node.
  */
class RDFLiteralNode(protected val _value: Any, protected val _language: Option[String] = None) extends RDFNode with
LiteralVertex
{
}
