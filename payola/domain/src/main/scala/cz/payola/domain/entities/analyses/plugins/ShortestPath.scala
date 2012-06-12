package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses._
import cz.payola.domain.rdf._
import scala.collection.mutable._

/** This filter returns a graph that consist of vertices that lie on the shortest
  * path between two vertices identified by URIs. If no such path exists, empty
  * graph is returned
  *
  */
class ShortestPath(id: String)
    extends Plugin("Shotest Path Plugin", 2, List(new StringParameter("OriginURI", ""),
        new StringParameter("DestinationURI", "")), id)
{
    /** Creates and returns a new graph that only contains nodes and edges
      * on the shortest path from origin to destination.
      *
      * @param inGraph The original graph.
      * @param fromOrigin Origin of the path.
      * @param toDestination Destination of the path.
      * @return New graph.
      */
    private def createGraphWithShortestPath(inGraph: Graph, fromOrigin: String, toDestination: String): Graph = {
        // Using Dijkstra. Distances will be initialized at Int.MaxValue.
        // Only identified vertices are taken into account as we can't find shortest
        // path between literal vertices
        val distances = new HashMap[IdentifiedNode, Int]()
        val previous = new HashMap[IdentifiedNode, IdentifiedNode]()

        // We assume that the origin and destination vertices are indeed in the graph
        val origin: IdentifiedNode = inGraph.getVertexWithURI(fromOrigin).get
        val destination: IdentifiedNode = inGraph.getVertexWithURI(toDestination).get

        inGraph.vertices foreach { n: Node =>
            if (n.isInstanceOf[IdentifiedNode]) {
                distances.put(n.asInstanceOf[IdentifiedNode], Int.MaxValue)
            }
        }

        distances.put(origin, 0)

        val nodes = new ListBuffer[IdentifiedNode]()
        inGraph.vertices.foreach { n: Node =>
            if (n.isInstanceOf[IdentifiedNode]){
                nodes += n.asInstanceOf[IdentifiedNode]
            }
        }

        while (!nodes.isEmpty) {
            val n = getVertexWithSmallestDistance(nodes, distances)
            if (distances(n) == Int.MaxValue) {
                // There are no more reachable nodes -> break by clearing the queue
                nodes.clear()
            }else{
                // Remove node from the queue
                nodes -= n

                val neighbors = getVertexNeighbors(inGraph, n)
                neighbors foreach { neighbor: IdentifiedNode =>
                    val alt = distances(n) + 1
                    if (alt < distances(neighbor)) {
                        distances.put(neighbor, alt)
                        previous.put(neighbor, n)
                    }
                }

            }
        }

        // All done. Now to construct the graph
        val vertices = ListBuffer[IdentifiedNode]()
        val edges = ListBuffer[Edge]()
        vertices += destination // Need to go backwards

        var current = destination
        while (previous(current) != null && previous(current) != origin) {
            val prev = previous(current)
            vertices += prev

            // Find edge between these two
            val edge = inGraph.edges.find { e: Edge =>
                e.origin.uri == prev.uri && e.destination.isInstanceOf[IdentifiedNode] && e.destination.asInstanceOf[IdentifiedNode].uri == current.uri
            }

            assert(edge.isDefined, "Edge not defined between vertices!")

            edges += edge.get

            current = prev
        }

        // Need to add origin
        vertices += origin

        new Graph(vertices, edges)
    }

    /** Creates a new instance of a graph that contains only vertices along the shortest path from
      * OriginURI to DestinationURI.
      *
      * @param instance The corresponding instance.
      * @param inputs The input graphs.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the (0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluate(instance: PluginInstance, inputs: collection.IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        val definedInputs = getDefinedInputs(inputs)

        assert(definedInputs.size > 0, "This plugin requires some input!")

        // Only the first graph is used
        val g = definedInputs(0)
        val origin = getOriginURI(instance)
        val destination = getDestinationURI(instance)

        if (g.containsVertexWithURI(origin) && g.containsVertexWithURI(destination)) {
            createGraphWithShortestPath(g, origin, destination)
        }else{
            // Either origin or destination isn't present in the graph => return empty graph
            Graph.empty
        }
    }

    /** Gets the value of destination URI parameter instance.
      *
      * @param instance Parameter instance.
      * @return Destination URI.
      */
    def getDestinationURI(instance: PluginInstance): String = {
        val origin = instance.getStringParameter("DestinationURI")
        assert(origin.isDefined, "DestinationURI parameter must be defined")
        origin.get
    }

    /** Gets the value of origin URI parameter instance.
      *
      * @param instance Parameter instance.
      * @return OriginURI.
      */
    def getOriginURI(instance: PluginInstance): String = {
        val origin = instance.getStringParameter("OriginURI")
        assert(origin.isDefined, "OriginURI parameter must be defined")
        origin.get
    }

    /** Returns a list of neighbors of a vertex in a graph.
      *
      * @param inGraph Graph.
      * @param vertex Vertex.
      * @return Neighbors.
      */
    private def getVertexNeighbors(inGraph: Graph, vertex: IdentifiedNode): TraversableOnce[IdentifiedNode] = {
        // First get just the edges that go to identified vertices
        val edges: collection.Seq[Edge] = inGraph.getOutgoingEdges(vertex.uri).filter { e: Edge => e.destination.isInstanceOf[IdentifiedNode] }
        edges.map { e: Edge => e.destination.asInstanceOf[IdentifiedNode] }
    }

    /** Returns the vertex with smallest distance or null if the withinVertices is empty.
      *
      * @param withinVertices Vertices, within which to search.
      * @param withDistances Distances hash map.
      * @return Vertex with smallest distance.
      */
    private def getVertexWithSmallestDistance(withinVertices: Iterable[IdentifiedNode], withDistances: HashMap[IdentifiedNode, Int]): IdentifiedNode = {
        var smallest: IdentifiedNode = null
        withinVertices foreach { n: IdentifiedNode =>
            if (smallest == null) {
                smallest = n
            }else if (withDistances(n) < withDistances(smallest)){
                smallest = n
            }
        }
        smallest
    }


}
