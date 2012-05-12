package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.plugins.{SparqlQuery, DataFetcher}
import cz.payola.domain.entities.analyses.PluginInstance

class FetcherQueryPluginInstance(val dataFetcher: PluginWithInstance[DataFetcher],
    val sparqlQuery: PluginWithInstance[SparqlQuery])
    extends PluginInstance(FetcherQueryPlugin, Nil)
