package cz.payola.domain.entities.analyses.evaluation

import actors.{TIMEOUT, Actor}
import cz.payola.domain.rdf.Graph
import collection.mutable
import cz.payola.domain.entities.{AnalysisException, Analysis}
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.optimization.AnalysisOptimizer
import cz.payola.domain.Timer
import scala.util.control.ControlThrowable

class AnalysisEvaluation(private val analysis: Analysis, private val timeout: Option[Long]) extends Actor
{
    private val timer = new Timer(timeout, this)

    private val instanceEvaluations = new mutable.ArrayBuffer[InstanceEvaluation]

    private var progress: AnalysisEvaluationProgress = AnalysisEvaluationProgress(Nil, Map.empty, Nil, Map.empty)

    private var result: Option[AnalysisResult] = None

    def act() {
        timer.start()

        // Verify that the analysis may be evaluated.
        try {
            analysis.checkValidity()
        } catch {
            case throwable => finishEvaluation(Error(throwable, progress.errors))
        }

        // Optimize the analysis
        val optimizedAnalysis = AnalysisOptimizer.process(analysis)
        val instanceInputBindings = optimizedAnalysis.pluginInstanceInputBindings

        def startInstanceEvaluation(instance: PluginInstance, outputProcessor: Option[Graph] => Unit) {
            val evaluation = new InstanceEvaluation(instance, this, outputProcessor)
            instanceEvaluations += evaluation
            evaluation.start()

            // Start the preceding plugin evaluations.
            instanceInputBindings(instance).foreach { binding =>
                val instanceOutputProcessor = bindingOutputProcessor(evaluation, binding.targetInputIndex) _
                startInstanceEvaluation(binding.sourcePluginInstance, instanceOutputProcessor)
            }
        }

        // Start the evaluation of the analysis by starting the output plugin instance.
        progress = AnalysisEvaluationProgress(Nil, Map.empty, optimizedAnalysis.allOriginalInstances, Map.empty)
        startInstanceEvaluation(optimizedAnalysis.outputInstance.get, analysisOutputProcessor)

        loop {
            react {
                case InstanceEvaluationProgress(i, v) => {
                    optimizedAnalysis.originalInstances(i).foreach { originalInstance =>
                        progress = progress.withChangedProgress(originalInstance, v)
                    }
                }
                case InstanceEvaluationError(i, t) => {
                    progress = progress.withError(i, t)
                }
                case InstanceEvaluationInput(_, graph) => {
                    finishEvaluation(graph.map(g => Success(g, progress.errors)).getOrElse {
                        Error(new AnalysisException("The analysis ended with an empty result."), progress.errors)
                    })
                }
                case TIMEOUT => finishEvaluation(Timeout)
                case control: AnalysisEvaluationControl => processControlMessage(control)
            }
        }
    }

    def bindingOutputProcessor(targetEvaluation: InstanceEvaluation, targetInputIndex: Int)(output: Option[Graph]) {
        targetEvaluation ! InstanceEvaluationInput(targetInputIndex, output)
    }

    def analysisOutputProcessor(output: Option[Graph]) {
        this ! InstanceEvaluationInput(0, output)
    }

    /**
      * Processes analysis evaluation control messages.
      * @param message The control message to process.
      */
    private def processControlMessage(message: AnalysisEvaluationControl) {
        message match {
            case GetProgress => reply(progress)
            case GetResult => reply(result)
            case Stop if result.isEmpty => finishEvaluation(Stopped)
            case Terminate => {
                terminateDependentActors()
                exit()
            }
        }
    }

    /**
      * Finishes the analysis evaluation and starts to respond only to control messages.
      * @param analysisResult The result to finish the analysis evaluation with.
      */
    private def finishEvaluation(analysisResult: AnalysisResult) {
        terminateDependentActors()
        result = Some(analysisResult)

        // Respond only to control messages.
        loop {
            react {
                case control: AnalysisEvaluationControl => processControlMessage(control)
                case _ =>
            }
        }
    }

    /**
      * Terminates all the dependent actors.
      */
    private def terminateDependentActors() {
        timer ! None
        instanceEvaluations.foreach(_ ! None)
    }

    /**
      * Progress of the analysis evaluation.
      */
    def getProgress: AnalysisEvaluationProgress = {
        (this !? GetProgress).asInstanceOf[AnalysisEvaluationProgress]
    }

    /**
      * Result of the analysis evaluation. [[scala.None]] in case the evaluation hasn't finished yet.
      */
    def getResult: Option[AnalysisResult] = {
        (this !? GetResult).asInstanceOf[Option[AnalysisResult]]
    }

    /**
      * Whether the analysis evaluation has finished.
      */
    def isFinished: Boolean = {
        getResult.isDefined
    }
}
