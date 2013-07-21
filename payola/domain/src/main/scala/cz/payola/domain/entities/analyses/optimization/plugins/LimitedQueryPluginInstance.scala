package cz.payola.domain.entities.analyses.optimization.plugins

import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance
import cz.payola.domain.entities.plugins.concrete.query.Limit

/**
  * An instance of the fetcher query optimization plugin.
  * @param sparqlQuery The SPARQL query plugin instance with its plugin.
  */
class LimitedQueryPluginInstance(val sparqlQuery: PluginWithInstance[SparqlQuery],
    val limit: PluginWithInstance[Limit])
    extends PluginInstance(new LimitedQueryPlugin, Nil)

