package cz.payola.domain.rdf

/**
  * A node in the RDF graph which only contains a literal value.
  * @param _value Value of the node.
  * @param _language Language of the node.
  */
class LiteralNode(protected val _value: Any, protected val _language: Option[String] = None)
    extends Node with cz.payola.common.rdf.LiteralVertex
