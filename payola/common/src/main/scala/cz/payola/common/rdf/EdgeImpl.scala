package cz.payola.common.rdf

import cz.payola.scala2json.annotations.JSONPoseableClass

@JSONPoseableClass(otherClassName = "cz.payola.common.rdf.generic.Edge")
class EdgeImpl(override val origin: IdentifiedVertexImpl, override val destination: IdentifiedVertexImpl, val uri: String)
    extends cz.payola.common.rdf.generic.Edge {

    type VertexType = IdentifiedVertexImpl
    type IdentifiedVertexType = IdentifiedVertexImpl

}
