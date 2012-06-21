package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance
import collection.immutable

case class AnalysisEvaluationProgress(evaluatedInstances: immutable.Seq[PluginInstance],
    runningInstances: Map[PluginInstance, Double], pendingInstances: immutable.Seq[PluginInstance],
    errors: Map[PluginInstance, Throwable])
{
    /**
      * Percentual representation of the analysis evaluation progress.
      */
    def value: Double = {
        val evaluatedInstanceCount = int2double(evaluatedInstances.length)
        val unfinishedInstanceCount = int2double(runningInstances.toList.length + pendingInstances.length)
        val instanceCount = evaluatedInstanceCount + unfinishedInstanceCount
        (evaluatedInstanceCount + runningInstances.map(_._2).sum) / instanceCount
    }

    private[entities] def withChangedProgress(instance: PluginInstance, value: Double): AnalysisEvaluationProgress = {
        if (!evaluatedInstances.contains(instance)) {
            var evaluated = evaluatedInstances
            var running = runningInstances
            var pending = pendingInstances

            if (pendingInstances.contains(instance)) {
                running = running + (instance -> value)
                pending = pending.diff(List(instance))
            }
            if (value == 1.0) {
                evaluated = instance +: evaluated
                running = running - instance
            }

            AnalysisEvaluationProgress(evaluated, running, pending, errors)
        } else {
            this
        }
    }

    private[entities] def withError(instance: PluginInstance, throwable: Throwable): AnalysisEvaluationProgress = {
        AnalysisEvaluationProgress(evaluatedInstances, runningInstances, pendingInstances,
            errors + (instance -> throwable))
    }
}
