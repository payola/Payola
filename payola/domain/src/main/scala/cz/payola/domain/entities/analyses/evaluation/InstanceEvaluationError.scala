package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance

case class InstanceEvaluationError(instance: PluginInstance, throwable: Throwable)
