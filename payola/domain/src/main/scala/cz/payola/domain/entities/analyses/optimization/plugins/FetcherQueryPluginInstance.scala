package cz.payola.domain.entities.analyses.optimization.plugins

import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.analyses.optimization.plugins._
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance

/**
  * An instance of the fetcher query optimization plugin.
  * @param dataFetcher The data fetcher plugin instance with its plugin.
  * @param sparqlQuery The SPARQL query plugin instance with its plugin.
  */
class FetcherQueryPluginInstance(val dataFetcher: PluginWithInstance[DataFetcher],
    val sparqlQuery: PluginWithInstance[SparqlQuery])
    extends PluginInstance(FetcherQueryPlugin, Nil)

class FetcherLimitedQueryPluginInstance(val dataFetcher: PluginWithInstance[DataFetcher],
    val limitedSparqlQuery: PluginWithInstance[LimitedQueryPlugin])
    extends PluginInstance(FetcherQueryPlugin, Nil)

