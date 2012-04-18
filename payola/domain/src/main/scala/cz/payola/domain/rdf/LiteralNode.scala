package cz.payola.domain.rdf

class LiteralNode(protected val _value: Any, protected val _language: Option[String] = None)
    extends Node with cz.payola.common.rdf.LiteralVertex
