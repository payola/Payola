package cz.payola.domain.rdf

import cz.payola.common.rdf.Edge

/** This class represents an edge between two RDFNode objects. The origin must
  * be an identified node.
  *
  * @param _origin Origin of the edge.
  * @param _destination Destination of the edge.
  * @param _uri URI.
  */
class RDFEdge(protected val _origin: RDFIdentifiedNode, protected val _destination: RDFNode,
    protected val _uri: String) extends Edge
{
    type VertexType = RDFNode

    type IdentifiedVertexType = RDFIdentifiedNode
}
