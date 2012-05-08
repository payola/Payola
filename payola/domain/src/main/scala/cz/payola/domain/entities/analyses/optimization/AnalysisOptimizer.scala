package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.plugins.query.SparqlQueryPart
import cz.payola.domain.entities.analyses.plugins.{SparqlQuery, DataFetcher}
import collection.mutable

object AnalysisOptimizer
{
    /**
      * Optimizes the specified analysis.
      * @param analysis The analysis to optimize.
      * @return An evaluationally-equivalent optimized analysis.
      */
    def process(analysis: Analysis): Analysis = {
        mergeDataFetchersWithQueries(mergeSparqlQueryParts(analysis))
    }

    def mergeSparqlQueryParts(analysis: Analysis): Analysis = {
        val optimizedAnalysis = new Analysis(analysis.name, analysis.owner)
        val instanceInputBindings = analysis.pluginInstanceInputBindings

        def mergeQueryPartChains(instance: PluginInstance): PluginInstance = {
            instance.plugin match {
                case _: SparqlQueryPart => {
                    val (mergedInstance, source) = mergeQueryPartInstance(instance)
                    optimizedAnalysis.addPluginInstance(mergedInstance)
                    source.foreach(s => optimizedAnalysis.addBinding(mergeQueryPartChains(s), mergedInstance))
                    mergedInstance
                }
                case _ => {
                    optimizedAnalysis.addPluginInstance(instance)
                    instanceInputBindings(instance).foreach {binding =>
                        val mergedSource = mergeQueryPartChains(binding.sourcePluginInstance)
                        optimizedAnalysis.addBinding(mergedSource, instance, binding.targetInputIndex)
                    }
                    instance
                }
            }
        }

        def mergeQueryPartInstance(instance: PluginInstance): (MergedQueryPartsPluginInstance, Option[PluginInstance]) = {
            instanceInputBindings(instance).headOption.map(_.sourcePluginInstance) match {
                case Some(queryPartSource) if queryPartSource.plugin.isInstanceOf[SparqlQueryPart] => {
                    val (mergedSource, mergedSourceSource) = mergeQueryPartInstance(queryPartSource)
                    (mergedSource + instance, mergedSourceSource)
                }
                case source => (MergedQueryPartsPluginInstance.empty + instance, source)
            }
        }

        mergeQueryPartChains(analysis.outputInstance.get)
        optimizedAnalysis
    }

    def mergeDataFetchersWithQueries(analysis: Analysis): Analysis = {
        val optimizedAnalysis = new Analysis(analysis.name, analysis.owner)
        analysis.pluginInstances.foreach(instance => optimizedAnalysis.addPluginInstance(instance))
        analysis.pluginInstanceBindings.foreach(binding => optimizedAnalysis.addBinding(binding))

        optimizedAnalysis.pluginInstanceBindings.foreach{binding =>
            val sourceInstance = binding.sourcePluginInstance
            val targetInstance = binding.targetPluginInstance

            sourceInstance.plugin match {
                case dataFetcher: DataFetcher => targetInstance.plugin match {
                    case sparqlQuery: SparqlQuery => {
                        val outputBinding = optimizedAnalysis.pluginInstanceOutputBindings(targetInstance).headOption

                        // Remove the old instances with bindings.
                        optimizedAnalysis.removePluginInstance(sourceInstance)
                        optimizedAnalysis.removePluginInstance(targetInstance)

                        // Add the new one which wraps both removed instances.
                        val newInstance = new DataFetcherWithQueryPluginInstance(
                            TypedPluginInstance(dataFetcher, sourceInstance),
                            TypedPluginInstance(sparqlQuery, targetInstance)
                        )
                        optimizedAnalysis.addPluginInstance(newInstance)
                        outputBinding.foreach {b =>
                            optimizedAnalysis.addBinding(newInstance, b.targetPluginInstance, b.targetInputIndex)
                        }
                    }
                }
            }
        }

        optimizedAnalysis
    }
}
