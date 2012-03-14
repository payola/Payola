package cz.payola.data.model.graph

import cz.payola.common.rdf.LiteralVertex

class RDFLiteralNode(val value: Any, val language: Option[String] = None) extends RDFNode with LiteralVertex
{
}
