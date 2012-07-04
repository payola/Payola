package cz.payola.domain.entities.analyses.optimization.phases

import cz.payola.domain.entities.analyses.optimization._
import cz.payola.domain.entities.analyses.optimization.plugins.ConstructJoinPluginInstance
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.concrete.query.Construct

/**
  * Merges joins of two construct plugin instances that receive data from the same data fetcher.
  */
class MergeJoins extends OptimizationPhase
{
    def run(analysis: OptimizedAnalysis): OptimizedAnalysis = {
        merge(analysis, analysis.outputInstance.get)
        analysis
    }

    /**
      * Merge the specified plugin instance with preceding instance in case it's possible. Merges all the preceding
      * instances with their predecessors recursively.
      * @param analysis The analysis where the merge is performed.
      * @param instance The instance to merge.
      */
    def merge(analysis: OptimizedAnalysis, instance: PluginInstance) {
        analysis.pluginInstanceInputBindings(instance).foreach(b => merge(analysis, b.sourcePluginInstance))
        instance.plugin match {
            case join: Join => {
                val instanceInputBindings = analysis.pluginInstanceInputBindings
                val outputBindings = analysis.pluginInstanceOutputBindings(instance)

                // Matches chains consisting af a DataFetcher and a Construct above the Join. For example:
                //
                //     DataFetcher1 --> Construct1 --> |------|
                //                                     | Join | --> Whatever
                //     DataFetcher2 --> Construct2 --> |------|
                //
                // will result in: List((Construct1, DataFetcher1), (Construct2, DataFetcher2))
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

                // If there are two chains with same data source, merges the constructs and the join into one plugin
                // instance. The above example will result in:
                //
                //     DataFetcher1 --> ConstructJoin --> Whatever
                //
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
}
