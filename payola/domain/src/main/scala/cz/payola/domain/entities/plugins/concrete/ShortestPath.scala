package cz.payola.domain.entities.plugins.concrete

import scala.collection._
import cz.payola.common.rdf._
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf.PayolaGraph
import cz.payola.domain.rdf.JenaGraph
import cz.payola.domain.rdf.Graph

/** This filter returns a graph that consist of vertices that lie on the shortest
  * path between two vertices identified by URIs. If no such path exists, empty
  * graph is returned
  *
  */
class ShortestPath(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def this() = {
        this("Shortest Path", 1, List(
            new StringParameter(ShortestPath.originURIParameter, "", false, false, false, true, Some(0)),
            new StringParameter(ShortestPath.destinationURIParameter, "", false, false, false, true, Some(1))
        ), IDGenerator.newId)
    }

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
        val distances = new mutable.HashMap[IdentifiedVertex, Int]()
        val previous = new mutable.HashMap[IdentifiedVertex, IdentifiedVertex]()

        // We assume that the origin and destination vertices are indeed in the graph
        val origin: IdentifiedVertex = inGraph.getVertexWithURI(fromOrigin).get
        val destination: IdentifiedVertex = inGraph.getVertexWithURI(toDestination).get

        inGraph.vertices foreach { n: Vertex =>
            if (n.isInstanceOf[IdentifiedVertex]) {
                distances.put(n.asInstanceOf[IdentifiedVertex], Int.MaxValue)
            }
        }

        distances.put(origin, 0)

        val nodes = new mutable.ListBuffer[IdentifiedVertex]()
        inGraph.vertices.foreach { n: Vertex =>
            if (n.isInstanceOf[IdentifiedVertex]) {
                nodes += n.asInstanceOf[IdentifiedVertex]
            }
        }

        while (!nodes.isEmpty) {
            val n = getVertexWithSmallestDistance(nodes, distances)
            if (distances(n) == Int.MaxValue) {
                // There are no more reachable nodes -> break by clearing the queue
                nodes.clear()
            } else {
                // Remove node from the queue
                nodes -= n

                val neighbors = getVertexNeighbors(inGraph, n)
                neighbors foreach { neighbor: IdentifiedVertex =>
                    val alt = distances(n) + 1
                    if (alt < distances(neighbor)) {
                        distances.put(neighbor, alt)
                        previous.put(neighbor, n)
                    }
                }
            }
        }

        // All done. Now to construct the graph
        val vertices = mutable.ListBuffer[IdentifiedVertex]()
        val edges = mutable.ListBuffer[Edge]()
        vertices += destination // Need to go backwards

        var current = destination
        while (previous(current) != null && previous(current) != origin) {
            val prev = previous(current)
            vertices += prev

            // Find edge between these two
            val edge = inGraph.edges.find { e: Edge =>
                e.origin.uri == prev.uri && e.destination.isInstanceOf[IdentifiedVertex] && e.destination
                    .asInstanceOf[IdentifiedVertex].uri == current.uri
            }

            assert(edge.isDefined, "Edge not defined between vertices!")

            edges += edge.get

            current = prev
        }

        // Need to add origin
        vertices += origin

        JenaGraph(new PayolaGraph(vertices.toList, edges.toList, None))
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
    def evaluate(instance: PluginInstance, inputs: collection.IndexedSeq[Option[Graph]],
        progressReporter: Double => Unit) = {

        usingDefined(inputs(0)) { g =>
            val origin = getOriginURI(instance)
            val destination = getDestinationURI(instance)

            if (g.containsVertexWithURI(origin) && g.containsVertexWithURI(destination)) {
                createGraphWithShortestPath(g, origin, destination)
            } else {
                // Either origin or destination isn't present in the graph => return empty graph
                JenaGraph.empty
            }
        }
    }

    /** Gets the value of destination URI parameter instance.
      *
      * @param instance Parameter instance.
      * @return Destination URI.
      */
    def getDestinationURI(instance: PluginInstance): String = {
        val origin = instance.getStringParameter(ShortestPath.destinationURIParameter)
        assert(origin.isDefined, "DestinationURI parameter must be defined")
        origin.get
    }

    /** Gets the value of origin URI parameter instance.
      *
      * @param instance Parameter instance.
      * @return OriginURI.
      */
    def getOriginURI(instance: PluginInstance): String = {
        val origin = instance.getStringParameter(ShortestPath.originURIParameter)
        assert(origin.isDefined, "OriginURI parameter must be defined")
        origin.get
    }

    /** Returns a list of neighbors of a vertex in a graph.
      *
      * @param inGraph Graph.
      * @param vertex Vertex.
        * @return Neighbors.
    */
    private def getVertexNeighbors(inGraph: Graph, vertex: IdentifiedVertex): TraversableOnce[IdentifiedVertex] = {
        // First get just the edges that go to identified vertices
        val edges: collection.Seq[Edge] = inGraph.getOutgoingEdges(vertex.uri)
            .filter { e: Edge => e.destination.isInstanceOf[IdentifiedVertex]}
        edges.map { e: Edge => e.destination.asInstanceOf[IdentifiedVertex]}
    }

    /** Returns the vertex with smallest distance or null if the withinVertices is empty.
     *
     * @param withinVertices Vertices, within which to search.
     * @param withDistances Distances hash map.
     * @return Vertex with smallest distance.
     */
    private def getVertexWithSmallestDistance(withinVertices: Iterable[IdentifiedVertex],
        withDistances: mutable.HashMap[IdentifiedVertex, Int]): IdentifiedVertex = {
        var smallest: IdentifiedVertex = null
        withinVertices foreach { n: IdentifiedVertex =>
            if (smallest == null) {
                smallest = n
            } else if (withDistances(n) < withDistances(smallest)) {
                smallest = n
            }
        }
        smallest
    }
}

object ShortestPath
{
    val originURIParameter = "Origin URI"

    val destinationURIParameter = "Destination URI"
}
