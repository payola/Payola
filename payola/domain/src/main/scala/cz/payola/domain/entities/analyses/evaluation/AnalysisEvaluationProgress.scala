package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance
import collection.immutable

/**
  * A progress of an analysis evaluation.
  * @param evaluatedInstances The plugin instances that have already been evaluate.
  * @param runningInstances The plugin instances that are being evaluated with their progress values.
  * @param pendingInstances The plugin instances that wait for their evaluation, because they haven't received all
  *                         inputs yet.
  * @param errors The plugin instances whose evaluations caused errors with the error objects.
  */
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

    /**
      * Returns a new analysis evaluation progress based on the current one with different progress of the specified
      * plugin instance.
      * @param instance The plugin instance, whose progress changed.
      * @param value The new value of plugin instance progress.
      * @return The new analysis evaluation progress.
      */
    private[evaluation] def withChangedProgress(instance: PluginInstance, value: Double): AnalysisEvaluationProgress = {
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
            } else {
                running = running.updated(instance, value)
            }

            AnalysisEvaluationProgress(evaluated, running, pending, errors)
        } else {
            this
        }
    }

    /**
      * Returns a new analysis evaluation progress based on the current one with an error of the specified plugin
      * instance.
      * @param instance The plugin instance, whose evaluation caused an error.
      * @param throwable The error object.
      * @return The new analysis evaluation progress.
      */
    private[evaluation] def withError(instance: PluginInstance, throwable: Throwable): AnalysisEvaluationProgress = {
        AnalysisEvaluationProgress(evaluatedInstances, runningInstances, pendingInstances,
            errors + (instance -> throwable))
    }
}
