package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.entities.plugins.PluginInstance

case class InstanceEvaluationProgress(instance: PluginInstance, value: Double)
{
    require(value >= 0.0 && value <= 1.0, "The progress value has to be within [0.0, 1.0] interval.")
}
