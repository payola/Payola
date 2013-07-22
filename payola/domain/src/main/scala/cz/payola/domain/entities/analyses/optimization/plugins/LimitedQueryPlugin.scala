package cz.payola.domain.entities.analyses.optimization.plugins

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph
/**
  * A plugin that during optimization replaces a data fetcher followed by a SPARQL query.
  */
class LimitedQueryPlugin extends Plugin("SPARQL query with a LIMIT statement", 1, Nil)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        instance match {
            case limitedQuery: LimitedQueryPluginInstance => {
                val definedInputs = getDefinedInputs(inputs)
                definedInputs(0).executeSPARQLQuery(getQueryString(limitedQuery))
            }
            case _ => throw new PluginException("The specified plugin instance doesn't correspond to the plugin.")
        }
    }

    def getQueryString(limitedQuery: LimitedQueryPluginInstance) : String = {
        val sparqlQuery = limitedQuery.sparqlQuery
        val query = sparqlQuery.plugin.getQuery(sparqlQuery.instance)
        val limit = limitedQuery.limit

        limit.plugin.getLimitCount(limit.instance).map { l =>
            query + " LIMIT %d".format(l)
        }.getOrElse(query)
    }
}
