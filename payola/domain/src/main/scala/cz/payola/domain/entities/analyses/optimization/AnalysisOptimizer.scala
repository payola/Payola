package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.query.Construct

object AnalysisOptimizer
{
    private val phases = List(
        mergeConstructs _,
        mergeSparqlQueryPartJoins _,
        mergeDataFetchersWithQueries _
    )

    /**
      * Optimizes the specified analysis.
      * @param analysis The analysis to optimize.
      * @return An evaluationally-equivalent optimized analysis.
      */
    def process(analysis: Analysis): OptimizedAnalysis = {
        phases.foldLeft[OptimizedAnalysis](new OptimizedAnalysis(analysis))((a, phase) => phase(a))
    }

    def mergeConstructs(analysis: OptimizedAnalysis): OptimizedAnalysis = {
        def merge(instance: PluginInstance) {
            val inputBindings = analysis.pluginInstanceInputBindings(instance)
            inputBindings.headOption.foreach { binding =>
                val source = binding.sourcePluginInstance
                val target = binding.targetPluginInstance
                if (source.plugin.isInstanceOf[Construct] && target.plugin.isInstanceOf[Construct]) {
                    val collapsedInstance = target match {
                        case targetConstructs: MultipleConstructsPluginInstance => targetConstructs + source
                        case _ => MultipleConstructsPluginInstance(target, source)
                    }
                    analysis.collapseBinding(binding, collapsedInstance)
                    merge(collapsedInstance)
                } else {
                    inputBindings.foreach(binding => merge(binding.sourcePluginInstance))
                }
            }
        }

        merge(analysis.outputInstance.get)
        analysis
    }

    def mergeSparqlQueryPartJoins(analysis: OptimizedAnalysis): OptimizedAnalysis = {
        def merge(instance: PluginInstance) {
            analysis.pluginInstanceInputBindings(instance).foreach(b => merge(b.sourcePluginInstance))
            instance.plugin match {
                case join: Join => {
                    val instanceInputBindings = analysis.pluginInstanceInputBindings
                    val outputBindings = analysis.pluginInstanceOutputBindings(instance)

                    // Matches chains consisting af a DataFetcher and a Construct above the Join.
                    val chains = instanceInputBindings(instance).sortBy(_.targetInputIndex).flatMap { binding =>
                        val source = binding.sourcePluginInstance
                        val sourceOfSource = instanceInputBindings(source).headOption.map(_.sourcePluginInstance)
                        sourceOfSource.map(_.plugin).flatMap {
                            case _: DataFetcher if source.plugin.isInstanceOf[Construct] => {
                                source.plugin match {
                                    case c: Construct => Some(PluginWithInstance(c, source), sourceOfSource.get)
                                    case _ => None
                                }
                            }
                            case _ => None
                        }
                    }

                    chains.toList match {
                        case List((subjectConstruct, subjectFetcher), (objectConstruct, objectFetcher)) => {
                            val dataFetcherParametersAreEqual = subjectFetcher.parameterValues.forall { value =>
                                objectFetcher.getParameter(value.parameter.name).map(_ == value.value).getOrElse(false)
                            }
                            if (subjectFetcher.plugin == objectFetcher.plugin && dataFetcherParametersAreEqual) {
                                // Merge the instances.
                                val joinInstance = new ConstructJoinPluginInstance(PluginWithInstance(join, instance),
                                    subjectConstruct, objectConstruct)
                                analysis.replaceInstances(joinInstance, objectFetcher, subjectConstruct.instance,
                                    objectConstruct.instance, instance)

                                // Restore the bindings.
                                analysis.addBinding(subjectFetcher, joinInstance)
                                outputBindings.foreach { b =>
                                    analysis.addBinding(joinInstance, b.targetPluginInstance, b.targetInputIndex)
                                }
                            }
                        }
                        case _ =>
                    }
                }
                case _ =>
            }
        }

        merge(analysis.outputInstance.get)
        analysis
    }

    def mergeDataFetchersWithQueries(analysis: OptimizedAnalysis): OptimizedAnalysis = {
        analysis.pluginInstanceBindings.foreach { binding =>
            val source = binding.sourcePluginInstance
            val target = binding.targetPluginInstance

            source.plugin match {
                case dataFetcher: DataFetcher => target.plugin match {
                    case sparqlQuery: SparqlQuery => {
                        analysis.collapseBinding(binding, new FetcherQueryPluginInstance(
                            PluginWithInstance(dataFetcher, source), PluginWithInstance(sparqlQuery, target)))
                    }
                    case _ =>
                }
                case _ =>
            }
        }

        analysis
    }
}
