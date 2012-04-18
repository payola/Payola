package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.Plugin

object PluginEvaluationProgress
{
    def apply(plugins: collection.Seq[Plugin], evaluatedPluginCount: Int): PluginEvaluationProgress = {
        require(evaluatedPluginCount >= 0 && evaluatedPluginCount <= plugins.length,
            "Cannot instantiate the PluginEvaluationProgress, the evaluatedPluginCount is invalid.")

        PluginEvaluationProgress(plugins.take(evaluatedPluginCount), plugins.drop(evaluatedPluginCount))
    }
}

case class PluginEvaluationProgress(evaluatedPlugins: collection.Seq[Plugin], unfinishedPlugins: collection.Seq[Plugin])
{
    private var currentPluginProgress: Option[Double] = None

    def currentPluginProgress_=(progress: Double) {
        require(progress >= 0.0 && progress <= 1.0, "Cannot set the current plugin progress, the progress is invalid.")

        currentPluginProgress = progress
    }

    def isFinished: Boolean = unfinishedPlugins.isEmpty

    def value: Double = {
        if (unfinishedPlugins.isEmpty) {
            1.0
        } else {
            val singlePluginProgressSignificance: Double = 1.0 / (evaluatedPlugins.length + unfinishedPlugins.length)
            (evaluatedPlugins.length / unfinishedPlugins.length) +
                (singlePluginProgressSignificance * currentPluginProgress.getOrElse(0.0))
        }
    }
}
