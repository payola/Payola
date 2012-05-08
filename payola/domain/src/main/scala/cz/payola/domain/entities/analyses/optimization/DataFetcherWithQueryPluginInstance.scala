package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.plugins.{SparqlQuery, DataFetcher}
import cz.payola.domain.entities.analyses.PluginInstance

class DataFetcherWithQueryPluginInstance(val dataFetcher: TypedPluginInstance[DataFetcher],
    val sparqlQuery: TypedPluginInstance[SparqlQuery])
    extends PluginInstance(DataFetcherWithQueryPlugin, Nil)
