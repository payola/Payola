package cz.payola.domain.entities.analyses

/**
  * Progress of an analysis evaluation
  * @param evaluatedPluginInstances The plugin instances that have already been evaluated.
  * @param currentPluginInstance The plugin instance that is being evaluated.
  * @param currentPluginInstanceProgress Progress of the current plugin instance evaluation.
  * @param unfinishedPluginInstances The plugin instances that haven't been evaluated yet.
  */
case class AnalysisEvaluationProgress(
    evaluatedPluginInstances: Seq[PluginInstance],
    currentPluginInstance: Option[PluginInstance],
    currentPluginInstanceProgress: Option[Double],
    unfinishedPluginInstances: Seq[PluginInstance])
{
    /**
      * Percentual representation of the analysis evaluation progress expressed as
      * (evaluated instance count / instance count) + current instance absolute progress.
      */
    def value: Double = {
        val unfinishedInstanceCount = unfinishedPluginInstances.length + currentPluginInstance.toList.length
        val instanceCount = evaluatedPluginInstances.length + unfinishedInstanceCount
        val currentInstanceAbsoluteProgress: Double = currentPluginInstanceProgress.getOrElse(0.0) / instanceCount
        (int2double(evaluatedPluginInstances.length) / instanceCount) + currentInstanceAbsoluteProgress
    }

    /**
      * Returns a new analysis evaluation progress with same distribution of the plugin instances, but with different
      * current plugin progress.
      * @param value The new value of the current plugin progress.
      */
    private[entities] def withIncreasedProgress(value: Double): AnalysisEvaluationProgress = {
        require(currentPluginInstance.isDefined, "The current plugin instance is undefined.")
        require(currentPluginInstanceProgress.forall(_ < value), "The progress has to be increased.")

        AnalysisEvaluationProgress(evaluatedPluginInstances, currentPluginInstance, Some(value),
            unfinishedPluginInstances)
    }

    /**
      * Returns a new analysis evaluation progress corresponding to the beginning of the next plugin instance
      * evaluation.
      */
    def nextStepStartingProgress: AnalysisEvaluationProgress = {
        val evaluated = evaluatedPluginInstances ++ currentPluginInstance.toList
        val current = unfinishedPluginInstances.headOption
        val unfinished = if (unfinishedPluginInstances.isEmpty) Nil else unfinishedPluginInstances.tail
        AnalysisEvaluationProgress(evaluated, current, None, unfinished)
    }
}
