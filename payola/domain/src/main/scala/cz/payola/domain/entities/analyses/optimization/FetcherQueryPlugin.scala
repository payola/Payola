package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph

object FetcherQueryPlugin extends Plugin("Mltiple merged SPARQL query parts", 0, Nil)
{
    override def createInstance(): PluginInstance = {
        throw new UnsupportedOperationException(
            "The FetcherQueryPlugin has to be instantiated directly using the constructor.")
    }

    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        instance match {
            case dataFetcherWithQuery: FetcherQueryPluginInstance => {
                val sparqlQuery = dataFetcherWithQuery.sparqlQuery
                val query = sparqlQuery.plugin.getQuery(sparqlQuery.instance)
                val dataFetcher = dataFetcherWithQuery.dataFetcher
                dataFetcher.plugin.evaluateWithQuery(dataFetcher.instance, query, progressReporter)
            }
            case _ => throw new PluginException("The specified plugin instance doesn't correspond to the plugin.")
        }
    }
}


