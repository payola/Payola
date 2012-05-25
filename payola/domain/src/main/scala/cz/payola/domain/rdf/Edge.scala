package cz.payola.domain.rdf

/** An edge between two Node objects. The origin must be an identified node.
  *
  * @param _origin Origin of the edge.
  * @param _destination Destination of the edge.
  * @param _uri URI.
  */
class Edge(protected val _origin: IdentifiedNode, protected val _destination: Node,
    protected val _uri: String)
    extends cz.payola.common.rdf.Edge
{
    type VertexType = Node

    type IdentifiedVertexType = IdentifiedNode
}
