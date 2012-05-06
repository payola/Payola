package cz.payola.domain.rdf

/**
  * A node in the RDF graph identified by the URI.
  * @param _uri URI of the node.
  */
class IdentifiedNode(protected val _uri: String) extends Node with cz.payola.common.rdf.IdentifiedVertex
