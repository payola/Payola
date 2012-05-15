package cz.payola.domain.rdf

/** Methods that are used for merging, etc.
  *
  */
private[rdf] object GraphHelper
{
    /** Returns whether collection c contains an edge between these two nodes with
      * such an URI.
      *
      * @param c Collection.
      * @param origin Origin.
      * @param destination Destination.
      * @param edgeURI Edge URI.
      * @return True of false.
      */
    def collectionContainsEdgeBetweenNodes(c: Traversable[Edge], origin: IdentifiedNode, destination: Node,
        edgeURI: String): Boolean = {
        c.find({e: Edge =>
            e.origin == origin && e.destination == destination && e.uri == edgeURI
        }).isDefined
    }

    /** Returns whether collection c contains a vertex with such properties.
      *
      * @param c Collection.
      * @param value Value.
      * @param language Language.
      * @return True or false.
      */
    def collectionContainsLiteralVertexWithValue(c: Traversable[Node], value: Any,
        language: Option[String] = None): Boolean = {
        getLiteralVertexWithValueFromCollection(c, value, language).isDefined
    }

    /** Returns whether collection c contains a vertex with such properties.
      *
      * @param c Collection.
      * @param vertexURI URI.
      * @return True or false.
      */
    def collectionContainsVertexWithURI(c: Traversable[Node], vertexURI: String): Boolean = {
        getVertexWithURIFromCollection(c, vertexURI).isDefined
    }

    /** Looks for a vertex with such properties in collection c.
      *
      * @param c Collection.
      * @param value Value.
      * @param language Language.
      * @return Vertex or None.
      */
    def getLiteralVertexWithValueFromCollection(c: Traversable[Node], value: Any,
        language: Option[String] = None): Option[LiteralNode] = {
        c.find({n: Node =>
            if (n.isInstanceOf[LiteralNode]) {
                val litNode = n.asInstanceOf[LiteralNode]
                if (litNode.value == value &&
                    ((language == None && litNode.language == None) || (language.get == litNode.language.get))) {
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }).asInstanceOf[Option[LiteralNode]]
    }

    /** Looks for a vertex with such URI in collection c.
      *
      * @param c Collection.
      * @param vertexURI URI of the vertex.
      * @return Vertex or None.
      */
    def getVertexWithURIFromCollection(c: Traversable[Node], vertexURI: String): Option[IdentifiedNode] = {
        c.find({n: Node =>
            if (n.isInstanceOf[IdentifiedNode] &&
                n.asInstanceOf[IdentifiedNode].uri == vertexURI) {
                true
            } else {
                false
            }
        }).asInstanceOf[Option[IdentifiedNode]]
    }
}
