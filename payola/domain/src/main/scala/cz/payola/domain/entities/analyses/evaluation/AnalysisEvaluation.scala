package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.Timer
import actors.{TIMEOUT, Actor}
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.Analysis
import collection.mutable
import cz.payola.domain.entities.analyses.{PluginInstance, AnalysisException}

class AnalysisEvaluation(private val analysis: Analysis, private val timeout: Option[Long]) extends Actor
{
    private val timer = new Timer(timeout, this)

    private val instanceEvaluations = new mutable.ArrayBuffer[InstanceEvaluation]

    private var _progress = AnalysisEvaluationProgress(Nil, Map.empty, analysis.pluginInstances.toList)

    private var _result: Option[AnalysisResult] = None

    def act() {
        try {
            timer.start()
            analysis.checkValidity()

            // TODO optimizations.

            val instanceInputBindings = analysis.pluginInstanceBindings.groupBy(_.targetPluginInstance)
            def startInstanceEvaluation(instance: PluginInstance, outputProcessor: Graph => Unit) {
                val evaluation = new InstanceEvaluation(instance, this, outputProcessor)
                instanceEvaluations += evaluation
                evaluation.start()

                // Start the preceding plugin evaluations.
                instanceInputBindings(instance).foreach {binding =>
                    val instanceOutputProcessor = bindingOutputProcessor(evaluation, binding.targetInputIndex) _
                    startInstanceEvaluation(binding.sourcePluginInstance, instanceOutputProcessor)
                }
            }

            startInstanceEvaluation(analysis.outputInstance.get, analysisOutputProcessor)

            loop {
                react {
                    case InstanceEvaluationProgress(i, v) => _progress = _progress.withChangedProgress(i, v)
                    case InstanceEvaluationError(i, t) => finishEvaluation(InstanceError(i, t))
                    case success: Success => finishEvaluation(success)
                    case TIMEOUT => finishEvaluation(Timeout)
                    case control: AnalysisEvaluationControl => processControlMessage(control)
                }
            }

        } catch {
            case e: AnalysisException => finishEvaluation(Error(e))
        }
    }

    def bindingOutputProcessor(targetPluginEvaluation: InstanceEvaluation, targetInputIndex: Int)(output: Graph) {
        targetPluginEvaluation ! InstanceEvaluationInput(targetInputIndex, output)
    }

    def analysisOutputProcessor(output: Graph) {
        this ! Success(output)
    }

    /**
      * Terminates all the dependent actors.
      */
    private def terminateDependentActors() {
        timer ! None
        instanceEvaluations.foreach(_ ! None)
    }

    /**
      * Processes analysis evaluation control messages.
      * @param message The control message to process.
      */
    private def processControlMessage(message: AnalysisEvaluationControl) {
        message match {
            case GetProgress => reply(_progress)
            case GetResult => reply(_result)
            case Stop if _result.isEmpty => finishEvaluation(Stopped)
            case Terminate => {
                terminateDependentActors()
                exit()
            }
        }
    }

    /**
      * Finishes the analysis evaluation and starts to respond only to control messages.
      * @param result The result to finish the analysis evaluation with.
      */
    private def finishEvaluation(result: AnalysisResult) {
        terminateDependentActors()
        _result = Some(result)

        // Respond only to control messages.
        loop {
            react {
                case control: AnalysisEvaluationControl => processControlMessage(control)
                case _ =>
            }
        }
    }

    /**
      * Progress of the analysis evaluation.
      */
    def progress: AnalysisEvaluationProgress = {
        (this !? GetProgress).asInstanceOf[AnalysisEvaluationProgress]
    }

    /**
      * Result of the analysis evaluation. [[scala.None.]] in case the evaluation hasn't finished yet.
      */
    def result: Option[AnalysisResult] = {
        (this !? GetResult).asInstanceOf[Option[AnalysisResult]]
    }

    /**
      * Whether the analysis evaluation has finished.
      */
    def isFinished: Boolean = {
        result.isDefined
    }
}
