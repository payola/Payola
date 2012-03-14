package cz.payola.data.model.graph

import collection.mutable.HashMap
import cz.payola.common.rdf.Edge

class RDFEdge(val origin: RDFIdentifiedNode, val destination: RDFNode, val uri: String) extends Edge
{

    type VertexType = RDFNode
    type IdentifiedVertexType = RDFIdentifiedNode
}
