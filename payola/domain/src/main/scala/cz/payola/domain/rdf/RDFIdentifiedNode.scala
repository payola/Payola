package cz.payola.domain.rdf

import cz.payola.common.rdf.IdentifiedVertex

/** A node in the RDF graph identified by the URI.
  *
  * @param _uri URI of the node.
  */
class RDFIdentifiedNode(protected val _uri: String) extends RDFNode with IdentifiedVertex
{
}
