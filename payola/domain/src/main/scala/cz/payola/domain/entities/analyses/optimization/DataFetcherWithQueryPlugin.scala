package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.{AnalysisException, PluginInstance}
import cz.payola.domain.entities.analyses.Plugin

object DataFetcherWithQueryPlugin extends Plugin("Mltiple merged SPARQL query parts", 0, Nil)
{
    override def createInstance(): PluginInstance = {
        throw new UnsupportedOperationException(
            "A new instance of the DataFetcherWithQueryPlugin cannot be created directly.")
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        instance match {
            case dataFetcherWithQuery: DataFetcherWithQueryPluginInstance => {
                val sparqlQuery = dataFetcherWithQuery.sparqlQuery
                val query = sparqlQuery.plugin.getQuery(sparqlQuery.instance)
                println(query)
                val dataFetcher = dataFetcherWithQuery.dataFetcher
                if (query.isDefined) {
                    dataFetcher.plugin.evaluateWithQuery(dataFetcher.instance, query.get, progressReporter)
                } else {
                    Graph.empty
                }
            }
            case _ => {
                throw new AnalysisException("The specified plugin instance doesn't correspond to the plugin.")
            }
        }
    }
}


