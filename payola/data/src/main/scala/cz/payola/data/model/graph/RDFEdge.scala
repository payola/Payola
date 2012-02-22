package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONPoseableClass

@JSONPoseableClass(otherClassName = "cz.payola.common.rdf.generic.Edge")
class RDFEdge(override val origin: RDFIdentifiedNode, override val destination: RDFNode, val uri: String)
    extends cz.payola.common.rdf.generic.Edge {

    type VertexType = RDFNode
    type IdentifiedVertexType = RDFIdentifiedNode

}
