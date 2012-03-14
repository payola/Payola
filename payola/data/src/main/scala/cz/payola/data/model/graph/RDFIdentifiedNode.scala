package cz.payola.data.model.graph

import cz.payola.common.rdf.IdentifiedVertex

class RDFIdentifiedNode(val uri: String) extends RDFNode with IdentifiedVertex
{
}
