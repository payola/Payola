package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.parameters._
import scala.collection.immutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.IDGenerator
import scala.collection.mutable.ListBuffer
import cz.payola.domain.rdf._

class Join(
    name: String = "Join",
    inputCount: Int = 2,
    parameters: immutable.Seq[Parameter[_]] = List(
        new StringParameter("JoinPropertyURI", ""),
        new BooleanParameter("IsInner", true)),
    id: String = IDGenerator.newId)
    extends Plugin(name, inputCount, parameters, id)
{
    /**
      * Returns "JoinPropertyURI" parameter value.
      * @param instance Plugin Instance.
      * @return The parameter value.
      */
    def getJoinPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("JoinPropertyURI")
    }

    /**
      * Returns "IsInner" parameter value.
      * @param instance Plugin instance.
      * @return The parameter value.
      */
    def getIsInner(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter("IsInner")
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
      * @param isInner Whether the join is inner (a node S from the first graph is included in the result only if there
      *                exists a node O in the second graph such that there exists an edge in the first graph with the
      *                specified URI connecting S and O) or outer (all nodes from the first graph are included in the
      *                result, but only those edges, that satisfy the condition of inner join mentioned above are
      *                included).
      * @return The joined graph.
      */
    private def joinGraphs(graph1: Graph, graph2: Graph, propertyURI: String, isInner: Boolean): Graph = {
        val resultVertices = new ListBuffer[Node]()
        val resultEdges = new ListBuffer[Edge]()

        val edges = graph1.edgesWithURI(propertyURI)
        edges.foreach { e: Edge =>
            if (graph2.containsVertex(e.origin)) {
                if (!resultVertices.contains(e.origin)) {
                    resultVertices += e.origin
                }
                if (!resultVertices.contains(e.destination)) {
                    resultVertices += e.destination
                }
                resultEdges += e
            } else if (!isInner) {
                if (!resultVertices.contains(e.origin)) {
                    resultVertices += e.origin
                }
            }
        }

        new Graph(resultVertices, resultEdges)
    }
}
