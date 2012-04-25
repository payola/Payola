package cz.payola.domain.entities.analyses.messages

case class PluginEvaluationProgress(value: Double)
{
    require(value >= 0.0 && value <= 1.0, "The progress value has to be within [0.0, 1.0] interval.")
}
