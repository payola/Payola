package cz.payola.domain.entities.analyses.evaluation

import actors.{TIMEOUT, Actor}
import collection.mutable
import cz.payola.domain.actors.Timer
import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.analyses.optimization._
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.optimization.phases._

/**
  * An actor that performs an analysis evaluation. It verifies and optimizes the analysis, starts all plugin instance
  * evaluations, takes care of sending the plugin instance evaluation outputs to appropriate inputs, tracks the time
  * spent evaluating, tracks the evaluation progress and responds to the control messages.
  * @param analysis The analysis to evaluate.
  * @param timeout The maximal time limit allowed for the evaluation to take in milliseconds.
  */
class AnalysisEvaluation(val analysis: Analysis, private val timeout: Option[Long]) extends Actor
{
    private val timer = new Timer(timeout, this)

    private val instanceEvaluations = new mutable.ArrayBuffer[InstanceEvaluation]

    private var progress: AnalysisEvaluationProgress = AnalysisEvaluationProgress(Nil, Map.empty, Nil, Map.empty)

    private var result: Option[AnalysisResult] = None

    def act() {
        val optimizedAnalysis = optimizeAnalysis()

        def startInstanceEvaluation(instance: PluginInstance, outputProcessor: Option[Graph] => Unit) {
            val evaluation = new InstanceEvaluation(instance, this, outputProcessor)
            instanceEvaluations += evaluation
            evaluation.start()

            // Start the preceding plugin evaluations.
            optimizedAnalysis.pluginInstanceInputBindings(instance).foreach { binding =>
                val instanceOutputProcessor = bindingOutputProcessor(evaluation, binding.targetInputIndex) _
                startInstanceEvaluation(binding.sourcePluginInstance, instanceOutputProcessor)
            }
        }

        // Start the evaluation of the analysis by starting the output plugin instance.
        timer.start()
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
                    optimizedAnalysis.originalInstances(i).foreach { originalInstance =>
                        progress = progress.withError(originalInstance, t)
                    }
                }
                case InstanceEvaluationInput(_, graph) => {
                    finishEvaluation(graph.map(g => Success(g, progress.errors)).getOrElse {
                        Error(
                            new AnalysisException("An error occured during evaluation of the analysis."),
                            progress.errors
                        )
                    })
                }
                case TIMEOUT => finishEvaluation(Timeout)
                case control: AnalysisEvaluationControl => processControlMessage(control)
            }
        }
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

    /**
      * Prepares the analysis before the actual evaluation.
      * @return The prepared optimized analysis.
      */
    private def optimizeAnalysis(): OptimizedAnalysis = {
        try {
            analysis.checkValidity()
        } catch {
            case throwable => finishEvaluation(Error(throwable, progress.errors))
        }

        val optimizer = new AnalysisOptimizer(List(
            new MergeConstructs,
            new MergeJoins,
            new MergeLimit,
            new MergeFetchersWithQueries
        ))
        optimizer.optimize(analysis)
    }

    /**
      * A function that takes a plugin instance evaluation output and sends it to the specified input.
      * @param targetEvaluation The target plugin instance evaluation.
      * @param targetInputIndex Index of the target plugin instance evaluation input.
      * @param output The output graph to send.
      */
    private def bindingOutputProcessor(targetEvaluation: InstanceEvaluation, targetInputIndex: Int)
        (output: Option[Graph]) {
        targetEvaluation ! InstanceEvaluationInput(targetInputIndex, output)
    }

    /**
      * A function that takes the output of the output plugin instance evaluation and sends it to the analysis
      * evaluation.
      * @param output The output graph to send.
      */
    private def analysisOutputProcessor(output: Option[Graph]) {
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
}
