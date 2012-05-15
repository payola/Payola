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
    /** Gets "JoinPropertyURI" parameter.
      *
      * @param instance Plugin Instance.
      * @return Parameter.
      */
    def getJoinPropertyUri(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("JoinPropertyURI")
    }

    /** Gets "IsInner" parameter.
      *
      * @param instance Plugin instance.
      * @return Parameter.
      */
    def getIsInner(instance: PluginInstance): Option[Boolean] = {
        instance.getBooleanParameter("IsInner")
    }

    /** Performs JOIN on inputs.
      *
      * @param instance The corresponding instance.
      * @param inputs The input graphs.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the [0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        val isInner = getIsInner(instance).getOrElse(false)
        evaluateJoin(instance, inputs, progressReporter, isInner).getOrElse(Graph.empty)
    }

    /** Performs JOIN on inputs.
      *
      * @param instance Plugin instance.
      * @param inputs Input graphs.
      * @param progressReporter Progress reporter (@see evaluate)
      * @param isInner Is inner join?
      * @return Output graph or None.
      */
    private def evaluateJoin(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit, isInner: Boolean): Option[Graph] = {
        getJoinPropertyUri(instance).flatMap { propertyURI =>
            var g = inputs.head
            for (i <- 1 until inputs.size) {
                progressReporter((1.0 * i) / inputs.size)
                g = joinGraphs(g, inputs(i), propertyURI, isInner)
            }
            Some(g)
        }
    }

    /** Actually joins two graphs together.
      *
      * @param graph1 First graph.
      * @param graph2 Second graph.
      * @param propertyURI Property URI.
      * @param isInner Inner join?
      * @return New graph.
      */
    private def joinGraphs(graph1: Graph, graph2: Graph, propertyURI: String, isInner: Boolean): Graph = {
        val resultVertices = new ListBuffer[Node]()
        val resultEdges = new ListBuffer[Edge]()

        val edges = graph1.edgesWithURI(propertyURI)
        edges foreach { e: Edge =>
            if (graph2.containsVertex(e.origin)) {
                if (!resultVertices.contains(e.origin)) {
                    resultVertices += e.origin
                }
                if (!resultVertices.contains(e.destination)) {
                    resultVertices += e.destination
                }
                resultEdges += e
            }else if (isInner) {
                if (!resultVertices.contains(e.origin)) {
                    resultVertices += e.origin
                }
            }
        }

        new Graph(resultVertices, resultEdges)
    }

}
