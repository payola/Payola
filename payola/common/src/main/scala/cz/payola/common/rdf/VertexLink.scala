package cz.payola.common.rdf

/**
 * Vertex representing a LiteralVertex object, that has not been transfered from server to client yet. It only contains
 * the URI of the LiteralVertex, that a fetch call to server can be performed, getting the detailed information (data)
 * about the LiteralVertex with the URI.
 * @param _vertexLinkURI
 */
class VertexLink(private var _vertexLinkURI: String) extends Vertex
{

    def vertexLinkURI = _vertexLinkURI

    override def toString = _vertexLinkURI
}
