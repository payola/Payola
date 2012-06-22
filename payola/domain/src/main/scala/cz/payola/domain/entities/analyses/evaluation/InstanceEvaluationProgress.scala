package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance

/**
  * A progress of a plugin instance evaluation during an analysis evaluation.
  * @param instance The plugin insatnce.
  * @param value Percentual value of the plugin evaluation progress.
  */
case class InstanceEvaluationProgress(instance: PluginInstance, value: Double)
{
    require(instance != null, "The instance mustn't be null.")
    require(value >= 0.0 && value <= 1.0, "The progress value has to be within [0.0, 1.0] interval.")
}
