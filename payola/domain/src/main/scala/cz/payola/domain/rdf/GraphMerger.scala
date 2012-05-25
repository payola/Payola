package cz.payola.domain.rdf

import scala.collection.mutable.ListBuffer

private[rdf] object GraphMerger {

    /** Merges two graphs together.
      *
      * @param g1 Graph 1.
      * @param g2 Graph 2.
      * @return New Graph instance.
      */
    def apply(g1: Graph, g2: Graph): Graph = {
        val merger = new GraphMerger(g1, g2)
        merger.merge
    }
}

/** A graph merger class.
  *
  * @param g1 Graph 1.
  * @param g2 Graph 2.
  */
private[rdf] class GraphMerger(g1: Graph, g2: Graph)
{
    /** Vertices of the new graph.
     */
    val vs: ListBuffer[Node] = new ListBuffer[Node]()

    /** Edges of he new graph.
      */
    val es: ListBuffer[Edge] = new ListBuffer[Edge]()

    /** Merged graph.
      */
    private var mergedGraph: Graph = null

    /** Creates a merged graph and saves it to the mergedGraph variable.
      */
    private def createMergedGraph() {
        // Copy over all vertices and edges from the first graph
        vs ++= g1.vertices
        es ++= g1.edges

        // Now begin the merge
        g2.vertices foreach ({n: Node =>
            mergeNode(n)
        })

        g2.edges foreach ({e: Edge =>
            mergeEdge(e)
        })

        mergedGraph = new Graph(vs, es)
    }

    /** Returns merged graph. May be called more than once, but will always return
      * the same instance.
      *
      * @return Merged graph.
      */
    def merge: Graph = {
        if (mergedGraph == null) {
            createMergedGraph()
        }
        mergedGraph
    }

    /** Merges in an edge.
      *
      * @param e Edge to be merged in.
      */
    private def mergeEdge(e: Edge) {
        val origin: Option[IdentifiedNode] = GraphHelper.getVertexWithURIFromCollection(vs, e.origin.uri)
        val destination: Option[Node] = if (e.destination.isInstanceOf[IdentifiedNode]) {
            GraphHelper.getVertexWithURIFromCollection(vs, e.destination.asInstanceOf[IdentifiedNode].uri)
        } else {
            val d = e.destination.asInstanceOf[LiteralNode]
            GraphHelper.getLiteralVertexWithValueFromCollection(vs, d.value, d.language)
        }

        assert(origin.isDefined && destination.isDefined, "Trying to merge an edge that " +
            "doesn't have both vertices in this graph (" + origin + " -> " + destination + ")")

        if (!GraphHelper.collectionContainsEdgeBetweenNodes(es, origin.get, destination.get, e.uri)) {
            es += new Edge(origin.get, destination.get, e.uri)
        }
    }

    /** Merges in a node n. If an equivalent node is already
      * among vertices of this graph, this method does nothing.
      *
      * @param n Node to be merged in.
      */
    private def mergeNode(n: Node) {
        n match {
            case identifiedNode: IdentifiedNode => {
                if (!GraphHelper.collectionContainsVertexWithURI(vs, identifiedNode.uri)) {
                    // This vertex is not in this graph, let's add it
                    vs += n
                }
            }
            case literalNode: LiteralNode => {
                if (!GraphHelper.collectionContainsLiteralVertexWithValue(vs, literalNode.value, literalNode.language)) {
                    vs += n
                }
            }
            case _ => throw new IllegalArgumentException("Unknown RDF graph node class - " + n)
        }
    }

}
