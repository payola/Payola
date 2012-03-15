package cz.payola.data.model.graph

import cz.payola.common.rdf.LiteralVertex

class RDFLiteralNode(protected val _value: Any, protected val _language: Option[String] = None) extends RDFNode with LiteralVertex
{
}
