package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.{PluginInstance, Plugin, Parameter}
import cz.payola.domain.rdf.Graph
import collection.immutable

abstract class DataFetcher(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    private val selectEverythingQuery = """
        CONSTRUCT {
            ?n1 ?p1 ?n2 .
        }
        WHERE {
            ?n1 ?p1 ?n2 .
        }
                                        """

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
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
    def evaluateWithQuery(instance: PluginInstance, query: String, progressReporter: Double => Unit): Graph
}
