package cz.payola.domain.entities.analyses.evaluation

import actors.Actor
import collection.mutable
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.plugins.PluginInstance

/**
  * An actor that evaluates the plugin instance during analysis evaluation. When started, it waits for all the inputs,
  * then evaluates the plugin instance and sends the output graph to a particular recipient using the outputProcessor.
  * @param instance The plugin instance to be evaluated.
  * @param analysisEvaluation The analysis evaluation that encapsulates the plugin instance evaluation.
  * @param outputProcessor A function that sends the evaluation output to a particular recipient.
  */
class InstanceEvaluation(private val instance: PluginInstance, private val analysisEvaluation: AnalysisEvaluation,
    private val outputProcessor: Option[Graph] => Unit)
    extends Actor
{
    def act() {
        if (instance.plugin.inputCount == 0) {
            // If the instance has no inputs, then it may be evaluated right away.
            evaluateInstance(Nil.toIndexedSeq)
        } else {
            // Wait for all the inputs, then evaluate the instance.
            val inputs = new mutable.ArrayBuffer[InstanceEvaluationInput]()
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
    }

    /**
      * Performs the plugin instance evaluation.
      * @param inputs The input graphs.
      */
    private def evaluateInstance(inputs: IndexedSeq[Option[Graph]]) {
        reportProgress(0.0)

        val output: Option[Graph] = try {
            Some(instance.plugin.evaluate(instance, inputs, reportCheckedProgress))
        } catch {
            // An error occurred, notify the analysis evaluation about it and return [[scala.None]] as the output.
            case throwable => {
                analysisEvaluation ! InstanceEvaluationError(instance, throwable)
                None
            }
        }

        reportProgress(1.0)
        outputProcessor(output)
    }

    /**
      * Reports the plugin instance evaluation progress to the analysis evaluation.
      * @param value Percentual value of the progress.
      */
    private def reportProgress(value: Double) {
        analysisEvaluation ! InstanceEvaluationProgress(instance, value)
    }

    /**
      * Reports the plugin instance evaluation progress to the analysis evaluation.
      * @param value Percentual value of the progress that has to be within the (0.0, 1.0] interval.
      */
    private def reportCheckedProgress(value: Double) {
        require(value > 0.0 && value <= 1.0, "The progress value has to be within (0.0, 1.0] interval.")

        // The value 1.0 is reserved for the representation of a plugin instance evaluation end. Because there won't
        // be no control over what a plugin reports, it might happen, that the plugin would report 1.0 but its
        // evaluation  would actually continue (false report). However it's easier for the implementers of plugins to be
        // able to report even 1.0. So the 1.0 reported by a plugin is internally treated as 0.999 so we're sure, that
        // the plugin instance evaluation is really done when 1.0 is reported to the analysis evaluation.
        reportProgress(if (value == 1.0) 0.999 else value)
    }
}
