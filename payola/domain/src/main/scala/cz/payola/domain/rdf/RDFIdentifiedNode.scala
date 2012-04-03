package cz.payola.domain.rdf

import cz.payola.common.rdf.IdentifiedVertex

class RDFIdentifiedNode(protected val _uri: String) extends RDFNode with IdentifiedVertex
{
}
