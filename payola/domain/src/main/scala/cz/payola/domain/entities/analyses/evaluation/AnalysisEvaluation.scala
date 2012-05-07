package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.Timer
import actors.{TIMEOUT, Actor}
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.{DataSource, Analysis}
import cz.payola.domain.entities.analyses.messages._

/**
  * An evaluation of an analysis. First of all, the initial graph is retrieved from the data sources using the sparql
  * query defined in the analysis initial sparql query plugin instance. The progress updates are received during whole
  * execution of the query. When all the data sources have returned either a successful result or an error, the
  * [[cz.payola.domain.entities.analyses.messages.QueryExecutionResult]] is received.
  *
  * The initial input graph is passed to the second plugin instance, which after evaluation returns another graph.
  * The outputted graph is used as an input for the third plugin instance and so on. During the evaluation of a plugin
  * instance the progress updates may be received. An exception of a plugin evaluation causes the analysis evaluation
  * to end up with error result.
  *
  * When the analysis evaluation finishes or is stopped by the user, it comes to a state in which it responds only to
  * control messages so the result or progress may be retrieved. To terminate the analysis evaluation, send it the
  * [[cz.payola.domain.entities.analyses.messages.TerminateEvaluation.]] message.
  *
  * @param analysis The analysis to evaluate.
  * @param dataSources The data sources used to provider the initial rdf data.
  * @param timeout Maximal time, the analysis evaluation may run.
  */
/*class AnalysisEvaluation(
    private val analysis: Analysis,
    private val dataSources: Seq[DataSource],
    private val timeout: Option[Long])
    extends Actor
{
    private val timer = new Timer(timeout, this)

    private var queryExecutionProgress = QueryExecutionProgress(Nil, Nil, dataSources)

    private var analysisEvaluationProgress =
        AnalysisEvaluationProgress(Nil, Some(analysis.initialPluginInstance), None, analysis.nonInitialPluginInstances)

    private var analysisEvaluationResult: Option[AnalysisEvaluationResult] = None

    /**
      * Whether the analysis evaluation has finished.
      */
    def isFinished: Boolean = {
        result.isDefined
    }

    /**
      * Result of the analysis evaluation. [[scala.None.]] in case the evaluation hasn't finished yet.
      */
    def result: Option[AnalysisEvaluationResult] = {
        (this !? GetResult).asInstanceOf[Option[AnalysisEvaluationResult]]
    }

    /**
      * Progress of the query execution.
      */
    def queryProgress: QueryExecutionProgress = {
        (this !? GetQueryExecutionProgress).asInstanceOf[QueryExecutionProgress]
    }

    /**
      * Progress of the analysis evaluation.
      */
    def progress: AnalysisEvaluationProgress = {
        (this !? GetAnalysisEvaluationProgress).asInstanceOf[AnalysisEvaluationProgress]
    }

    /**
      * Starts the query execution, the timer and waits for the query result. Then evaluates the plugin instances.
      */
    def act() {
        // Execute the query of the analysis initial sparql plugin.
        val queryExecution = new QueryExecution(this, dataSources, analysis.initialQuery)
        queryExecution.start()
        timer.start()

        loop {
            react {
                // A message with the current progress of the query execution. Just update the queryExecutionProgress.
                case progress: QueryExecutionProgress => {
                    analysisEvaluationProgress = analysisEvaluationProgress.withIncreasedProgress(progress.value)
                    queryExecutionProgress = progress
                }

                // Result of the query execution. Update the analysis evaluation progress to reflect that the initial
                // plugin has been evaluated (via the query execution). Start evaluating the other instances.
                case QueryExecutionResult(data) => {
                    analysisEvaluationProgress = analysisEvaluationProgress.nextStepStartingProgress
                    evaluatePluginInstances(analysis.nonInitialPluginInstances, Graph(data.head)) // TODO merge graphs
                }

                // Timeout during the query execution. Terminate the query execution and finish the evaluation.
                case TIMEOUT => {
                    queryExecution ! TIMEOUT
                    finishEvaluation(AnalysisEvaluationQueryTimeout(queryExecutionProgress))
                }

                case control: AnalysisEvaluationControl => processControlMessage(control)
                case message => println("Unknown message during query execution: " + message)
            }
        }
    }

    /**
      * Evaluates the specified sequence of plugin instances.
      * @param instances The plugin instances to evaluate.
      * @param inputGraph The input graph that is passed to the first plugin instance in the sequence.
      */
    private def evaluatePluginInstances(instances: Seq[PluginInstance], inputGraph: Graph) {
        if (instances.isEmpty) {
            // If there are no instances to evaluate, it means the analysis evaluation is done.
            finishEvaluation(AnalysisEvaluationSuccess(inputGraph))
        } else {
            val currentPluginInstance = instances.head
            currentPluginInstance.evaluate(this, inputGraph)

            loop {
                react {
                    // Optionally reported plugin progress, just update the analysisEvaluationProgress.
                    case PluginEvaluationProgress(value) => {
                        analysisEvaluationProgress = analysisEvaluationProgress.withIncreasedProgress(value)
                    }

                    // Successful evaluation of the plugin, update the progress and evaluate the rest of instances.
                    case PluginEvaluationSuccess(graph) => {
                        val unfinishedInstances = analysisEvaluationProgress.unfinishedPluginInstances
                        analysisEvaluationProgress = analysisEvaluationProgress.nextStepStartingProgress
                        evaluatePluginInstances(unfinishedInstances, graph)
                    }

                    // Error of the plugin evaluation, finish the analysis evaluation.
                    case PluginEvaluationError(throwable) => {
                        finishEvaluation(AnalysisEvaluationPluginError(currentPluginInstance, throwable))
                    }

                    // Timeout of the analysis evaluation. Finish the evaluation.
                    case TIMEOUT => {
                        finishEvaluation(AnalysisEvaluationPluginTimeout(analysisEvaluationProgress))
                    }

                    case control: AnalysisEvaluationControl => processControlMessage(control)
                    case message => println("Unknown message during plugin evaluation: " + message)
                }
            }
        }
    }

    /**
      * Processes analysis evaluation control messages.
      * @param message The control message to process.
      */
    private def processControlMessage(message: AnalysisEvaluationControl) {
        message match {
            case StopEvaluation => {
                // It makes sense to stop the analysis only if it's running (i.e. the result is empty).
                if (analysisEvaluationResult.isEmpty) {
                    finishEvaluation(AnalysisEvaluationStopped)
                }
            }
            case TerminateEvaluation => exit()
            case GetQueryExecutionProgress => reply(queryExecutionProgress)
            case GetAnalysisEvaluationProgress => reply(analysisEvaluationProgress)
            case GetResult => reply(analysisEvaluationResult)
        }
    }

    /**
      * Finishes the analysis evaluation and starts to respond only to control messages.
      * @param result The result to finish the analysis evaluation with.
      */
    private def finishEvaluation(result: AnalysisEvaluationResult) {
        analysisEvaluationResult = Some(result)

        // Stop the timer.
        timer ! None

        // Respond only to control messages.
        loop {
            react {
                case controlMessage: AnalysisEvaluationControl => processControlMessage(controlMessage)
                case _ =>
            }
        }
    }
}
*/
