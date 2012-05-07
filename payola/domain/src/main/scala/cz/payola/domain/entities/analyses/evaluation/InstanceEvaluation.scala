package cz.payola.domain.entities.analyses.evaluation

import actors.Actor
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.PluginInstance
import collection.mutable

class InstanceEvaluation(private val instance: PluginInstance, private val analysisEvaluation: AnalysisEvaluation,
    private val outputProcessor: Graph => Unit)
    extends Actor
{
    def act() {
        var inputs = new mutable.ArrayBuffer[InstanceEvaluationInput]()

        loop {
            react {
                case input: InstanceEvaluationInput => {
                    inputs += input
                    if (inputs.length == instance.plugin.inputCount) {
                        evaluateInstance(inputs.sortBy(_.index).map(_.value).toIndexedSeq)
                    }
                }
                case _ => exit()
            }
        }
    }

    def evaluateInstance(inputs: IndexedSeq[Graph]) {
        reportProgress(0.0)

        try {
            val output = instance.plugin.evaluate(instance, inputs, reportCheckedProgress)
            reportProgress(1.0)
            outputProcessor(output)
        } catch {
            case throwable => analysisEvaluation ! InstanceEvaluationError(instance, throwable)
        }
    }

    private def reportProgress(value: Double) {
        analysisEvaluation ! InstanceEvaluationProgress(instance, value)
    }

    private def reportCheckedProgress(value: Double) {
        require(value > 0.0 && value < 1.0, "The progress value has to be within (0.0, 1.0) interval.")
        reportProgress(value)
    }
}
