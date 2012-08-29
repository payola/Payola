package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import collection.mutable
import cz.payola.common.rdf._
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf.Graph

class Join(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def this() = {
        this("Join", 2, List(
            new StringParameter(Join.propertyURIParameter, "", false),
            new BooleanParameter(Join.isInnerParameter, true)
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
      */
    private def joinGraphs(graph1: Graph, graph2: Graph, propertyURI: String, isInner: Boolean): Graph = {
        val resultVertices = mutable.HashMap.empty[IdentifiedVertex, IdentifiedVertex]
        val resultEdges = mutable.HashSet.empty[Edge]
        def getOrElseInsertResultVertex(vertex: IdentifiedVertex) = resultVertices.getOrElseUpdate(vertex, vertex)

        graph1.edges.filter(_.uri == propertyURI).foreach { e =>
            if (graph2.vertices.contains(e.destination)) {
                e.destination match {
                    case i: IdentifiedVertex => {
                        val origin = getOrElseInsertResultVertex(e.origin)
                        val destination = getOrElseInsertResultVertex(i)
                        val resultEdge = new Edge(origin, destination, e.uri)
                        if (!resultEdges.contains(resultEdge)) {
                            resultEdges += resultEdge
                        }
                    }
                    case _ => // The destination isn't an identified vertex, therefore it's not joined.
                }
            } else if (!isInner) {
                getOrElseInsertResultVertex(e.origin)
            }
        }

        new Graph(resultVertices.keys.toList, resultEdges.toList)
    }
}

object Join
{
    val propertyURIParameter = "Join Property URI"

    val isInnerParameter = "Is Inner"
}
