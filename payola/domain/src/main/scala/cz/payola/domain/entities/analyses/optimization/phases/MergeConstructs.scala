package cz.payola.domain.entities.analyses.optimization.phases

import cz.payola.domain.entities.analyses.optimization._
import cz.payola.domain.entities.analyses.optimization.plugins._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete.query.Construct

/**
  * Merges multiple construct plugin instances into one.
  */
class MergeConstructs extends OptimizationPhase
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
                merge(analysis, collapsedInstance)
            } else {
                inputBindings.foreach(binding => merge(analysis, binding.sourcePluginInstance))
            }
        }
    }
}
