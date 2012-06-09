package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.{PluginInstance, Plugin, Parameter}
import cz.payola.domain.rdf.Graph
import collection.immutable

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

    private val selectFirstTripleQuery = selectEverythingQuery+" LIMIT 1"

    def getFirstTriple(instance: PluginInstance): Graph = {
        executeQuery(instance, selectFirstTripleQuery)
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        evaluateWithQuery(instance, selectEverythingQuery, progressReporter)
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
      * Returns neighbourhood of the specified node.
      * @param instance The corresponding instance.
      * @param nodeURI URI of the node whose neighbourhood should be returned.
      * @param distance Maximal distance to travel from the node to its neighbours. To select only direct neighbours,
      *                 use 1, to select direct neighbours and their neighbours, use 2 etc. Note that particular data
      *                 fetchers may use some optimizations/heuristics so it's not guaranteed that this parameter will
      *                 be always taken into account.
      * @return The neighbourhood graph.
      */
    def getNeighbourhood(instance: PluginInstance, nodeURI: String, distance: Int = 1): Graph
}
