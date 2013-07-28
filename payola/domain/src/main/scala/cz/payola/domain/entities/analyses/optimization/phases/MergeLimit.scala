package cz.payola.domain.entities.analyses.optimization.phases

import cz.payola.domain.entities.analyses.optimization._
import cz.payola.domain.entities.analyses.optimization.plugins._
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance
import cz.payola.domain.entities.plugins.parameters.StringParameterValue

/**
 * Handle optimalizations of Limit plugin.
 *
 * Merges data fetcher plugin instances with SPARQL queries.
 * @author Jiri Helmich
 */
class MergeLimit extends OptimizationPhase
{
     def run(analysis: OptimizedAnalysis): OptimizedAnalysis = {
         analysis.pluginInstanceBindings.foreach { binding =>
             val source = binding.sourcePluginInstance
             val target = binding.targetPluginInstance

             source.plugin match {
                 case c: Construct => //will merge in another phase
                 case q: SparqlQuery if !q.getQuery(source).toUpperCase.contains("LIMIT") => target.plugin match {
                     case limit: Limit => {
                         analysis.collapseBinding(binding, new LimitedQueryPluginInstance(PluginWithInstance(q, source), PluginWithInstance(limit, target)))
                     }
                     case _ =>
                 }
                 case _ =>
             }
         }

         analysis
     }
 }
