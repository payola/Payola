package cz.payola.common.rdf

class VertexLink(private var _vertexLinkURI: String) extends Vertex
{

    def vertexLinkURI = _vertexLinkURI

    override def toString = _vertexLinkURI
}
