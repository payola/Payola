package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.PluginInstance

class FetcherQueryPluginInstance(val dataFetcher: PluginWithInstance[DataFetcher],
    val sparqlQuery: PluginWithInstance[SparqlQuery])
    extends PluginInstance(FetcherQueryPlugin, Nil)
