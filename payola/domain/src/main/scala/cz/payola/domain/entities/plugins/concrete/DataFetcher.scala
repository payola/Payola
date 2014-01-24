package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.rdf.JenaGraph
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.concrete.query.Construct

/**
  * A plugin that is used to fetch RDF data using SPARQL queries, fetch neighbourhood of a particular node etc.
  */
abstract class DataFetcher(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    private val selectEverythingQuery = """
        CONSTRUCT {
            ?s ?p ?v .
        }
        WHERE {
            ?s ?p ?v .
        }"""

    private val selectFirstTripleQuery = selectEverythingQuery + " LIMIT 1"

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        // changed to be a construct query in order to enable optimalizations [Jiri Helmich]
        val list = List(TriplePattern(new Variable("s"),new Variable("p"),new Variable("o")))
        evaluateWithQuery(instance, ConstructQuery(GraphPattern(list)).toString, progressReporter)
    }

    /**
      * Evaluates the plugin.
      * @param instance The corresponding instance.
      * @param query The query used to filter data from the data source.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the [0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluateWithQuery(instance: PluginInstance, query: String, progressReporter: Double => Unit): Graph = {
        executeQuery(instance, query)
    }

    /**
      * Executes the specified query.
      * @param instance The corresponding instance.
      * @param query The query to execute.
      * @return The result of the query.
      */
    def executeQuery(instance: PluginInstance, query: String): Graph

    /**
      * Returns the first available triple.
      * @param instance The corresponding instance.
      * @return The triple represented as a graph.
      */
    def getFirstTriple(instance: PluginInstance): Graph = {
        executeQuery(instance, selectFirstTripleQuery)
    }

    /**
      * Returns neighbourhood of the specified vertex.
      * @param instance The corresponding instance.
      * @param vertexURI URI of the vertex whose neighbourhood should be returned.
      * @return The neighbourhood graph.
      */
    def getNeighbourhood(instance: PluginInstance, vertexURI: String): Graph = {

        val uri = vertexURI.trim

        if (uri.nonEmpty) {
            val subjectVariable = Variable("s1")
            val objectVariable = Variable("o1")

            val patterns = List(
                GraphPattern(List(TriplePattern(subjectVariable, Variable("sp0"), Uri(vertexURI))),
                    GraphPattern.optionalProperties(subjectVariable)
                ),
                GraphPattern(List(TriplePattern(Uri(vertexURI), Variable("op0"), objectVariable)),
                    GraphPattern.optionalProperties(objectVariable)
                )
            )

            patterns.par.map(p => executeQuery(instance, SelectCountQuery(p).toString)).reduce(_ + _)
        } else {
            JenaGraph.empty
        }
    }
}
