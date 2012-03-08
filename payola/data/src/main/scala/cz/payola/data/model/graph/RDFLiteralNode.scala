package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.LiteralVertex])
class RDFLiteralNode(override val value: Any, override val language: Option[String] = Option.empty[String])
    extends RDFNode with cz.payola.common.rdf.LiteralVertex
{
}
