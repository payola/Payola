package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import collection.mutable
import cz.payola.common.rdf._
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._
import cz.payola.domain.rdf.Graph

class Join(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def this() = {
        this("Join", 2, List(
            new StringParameter(Join.propertyURIParameter, "", false, false, false, true, Some(0)),
            new BooleanParameter(Join.isInnerParameter, true, Some(1))
        ), IDGenerator.newId)
    }

    def getJoinPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(Join.propertyURIParameter)
    }

    def getIsInner(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter(Join.isInnerParameter)
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        usingDefined(getJoinPropertyURI(instance), getIsInner(instance)) { (propertyURI, isInner) =>
            val definedInputs = getDefinedInputs(inputs)
            var result = definedInputs.head
            for (i <- 1 until inputs.size) {
                progressReporter((1.0 * i) / inputs.size)
                result = joinGraphs(result, definedInputs(i), propertyURI, isInner)
            }
            result
        }
    }

    /**
      * Actually joins two graphs together.
      * @param graph1 First graph.
      * @param graph2 Second graph.
      * @param propertyURI Property URI.
      * @param isInner Whether the join is inner (a vertex S from the first graph is included in the result only if
      *                there exists a vertex O in the second graph such that there exists an edge in the first graph
      *                with the specified URI connecting S and O) or outer (all vertices from the first graph are
      *                included in the result, but only those edges, that satisfy the condition of inner join mentioned
      *                above are included).
      * @return The joined graph.
      * TODO: use Jena Model
      */
    private def joinGraphs(graph1: Graph, graph2: Graph, propertyURI: String, isInner: Boolean): Graph = {
        val mergedGraph = graph1 + graph2
        val edgesByOrigin = mergedGraph.edges.groupBy(_.origin)
        val resultIdentifiedVertices = mutable.HashMap.empty[IdentifiedVertex, IdentifiedVertex]
        val resultLiteralVertices = mutable.ListBuffer.empty[LiteralVertex]
        val resultEdges = mutable.HashSet.empty[Edge]

        def addLiteralVertexToResult(vertex: LiteralVertex): LiteralVertex = {
            resultLiteralVertices += vertex
            vertex
        }

        def addIdentifiedVertexToResult(vertex: IdentifiedVertex): IdentifiedVertex = {
            val origin = resultIdentifiedVertices.getOrElseUpdate(vertex, vertex)

            // Add all edges going from the vertex.
            edgesByOrigin.get(origin).getOrElse(Nil).foreach { e =>
                val destination = e.destination match {
                    case l: LiteralVertex => addLiteralVertexToResult(l)
                    case i: IdentifiedVertex => addIdentifiedVertexToResult(i)
                }
                resultEdges += new Edge(origin, destination, e.uri)
            }
            origin
        }

        // Add only those vertices from the first graph, that pass the join condition.
        graph1.edges.filter(_.uri == propertyURI).foreach { e =>
            if (graph2.vertices.contains(e.destination) || !isInner) {
                addIdentifiedVertexToResult(e.origin)
            }
        }

        val payolaGraph = new PayolaGraph(resultIdentifiedVertices.keys.toList ++ resultLiteralVertices.toList, resultEdges.toList, None)
        JenaGraph(payolaGraph)
    }
}

object Join
{
    val propertyURIParameter = "Join Property URI"

    val isInnerParameter = "Is Inner"
}
