package cz.payola.domain.rdf

class Edge(protected val _origin: IdentifiedNode, protected val _destination: Node,
    protected val _uri: String)
    extends cz.payola.common.rdf.Edge
{
    type VertexType = Node

    type IdentifiedVertexType = IdentifiedNode
}
