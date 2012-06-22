package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance

/**
  * An error of the plugin instance evaluation during an analysis evaluation.
  * @param instance The plugin instance.
  * @param throwable The error that occurred.
  */
case class InstanceEvaluationError(instance: PluginInstance, throwable: Throwable)
