package cz.payola.domain.entities.analyses

import actors.Actor
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.messages._

/**
  * An evaluation of a plugin. It just executes the evaluate method on the plugin with parameter values retrieved from
  * the plugin instances.
  * @param invoker The invoker of the evaluation, who will receive the progress updates and the result.
  * @param pluginInstance The plugin instance that is evaluated.
  * @param inputGraph The input graph.
  */
class PluginEvaluation(private val invoker: Actor, private val pluginInstance: PluginInstance,
    private val inputGraph: Graph)
    extends Actor
{
    def act() {
        try {
            val parameterValues = pluginInstance.parameterValues
            val progressReporter = (value: Double) => invoker ! PluginEvaluationProgress(value)
            val outputGraph = pluginInstance.plugin.evaluate(inputGraph, parameterValues, progressReporter)
            invoker ! PluginEvaluationSuccess(outputGraph)
        } catch {
            case throwable => invoker ! PluginEvaluationError(throwable)
        }
    }
}
