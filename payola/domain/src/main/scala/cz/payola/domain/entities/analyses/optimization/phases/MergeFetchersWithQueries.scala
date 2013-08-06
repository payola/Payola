package cz.payola.domain.entities.analyses.optimization.phases

import cz.payola.domain.entities.analyses.optimization._
import cz.payola.domain.entities.analyses.optimization.plugins._
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance
import cz.payola.domain.entities.plugins.concrete._

/**
  * Merges data fetcher plugin instances with SPARQL queries.
  */
class MergeFetchersWithQueries extends OptimizationPhase
{
     def run(analysis: OptimizedAnalysis): OptimizedAnalysis = {
         analysis.pluginInstanceBindings.foreach { binding =>
             val source = binding.sourcePluginInstance
             val target = binding.targetPluginInstance

             source.plugin match {
                 case dataFetcher: DataFetcher => target.plugin match {
                     case sparqlQuery: SparqlQuery => {
                         analysis.collapseBinding(binding, new FetcherQueryPluginInstance(
                             PluginWithInstance(dataFetcher, source), PluginWithInstance(sparqlQuery, target)))
                     }
                     case _ => target match { // added by Jiri Helmich, handle already merged Query with Limit plugin
                         case limitedQuery: LimitedQueryPluginInstance => {
                             analysis.collapseBinding(binding, new FetcherLimitedQueryPluginInstance(
                                 PluginWithInstance(dataFetcher, source), PluginWithInstance(new LimitedQueryPlugin, target)
                             ))
                         }
                         case _ =>
                     }
                 }
                 case _ =>
             }
         }

         analysis
     }
 }
