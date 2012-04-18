package cz.payola.domain.entities.analyses

import cz.payola.domain.Timer
import messages.{QueryExecutionResult, QueryExecutionProgress}
import actors.{TIMEOUT, Actor}
import cz.payola.domain.entities.{DataSource, Analysis}
import cz.payola.domain.rdf.Graph

class AnalysisEvaluation(private val analysis: Analysis, private val dataSources: collection.Seq[DataSource],
    private val timeout: Option[Long])
    extends Actor
{
    private val plugins = analysis.pluginInstances.map(_.plugin)

    private var queryExecutionProgress = QueryExecutionProgress(Nil, Nil, dataSources)

    private var pluginEvaluationProgress = PluginEvaluationProgress(Nil, plugins)

    def act() {
        val invoker = sender
        val timer = new Timer(timeout, this)
        timer.start()
        executeInitialQuery()
    }

    private def executeInitialQuery() {
        val queryExecution = new QueryExecution(this, dataSources, analysis.initialQuery)
        queryExecution.start()

        // Wait for the query result and track the progress.
        loop {
            react {
                case progress: QueryExecutionProgress => queryExecutionProgress = progress
                case result: QueryExecutionResult => {
                    pluginEvaluationProgress = PluginEvaluationProgress(plugins, 1)
                    // TODO create graph from the result
                    evaluatePlugins(new Graph(Nil, Nil))
                }
                case TIMEOUT => {
                    queryExecution ! TIMEOUT
                    exit()
                }
            }
        }
    }

    private def evaluatePlugins(inputGraph: Graph) {

    }
}
