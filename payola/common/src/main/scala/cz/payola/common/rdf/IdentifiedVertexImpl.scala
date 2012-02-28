package cz.payola.common.rdf

import cz.payola.scala2json.annotations.JSONPoseableClass

@JSONPoseableClass(otherClassName = "cz.payola.common.rdf.IdentifiedVertex")
class IdentifiedVertexImpl(override val uri: String) extends VertexImpl with cz.payola.common.rdf.IdentifiedVertex {

}
