package cz.payola.domain.entities.analyses

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.messages.QueryExecutionProgress

abstract class AnalysisEvaluationResult

case class AnalysisEvaluationSuccess(outputGraph: Graph) extends AnalysisEvaluationResult

case class AnalysisEvaluationPluginError(pluginInstance: PluginInstance, throwable: Throwable)
    extends AnalysisEvaluationResult

case class AnalysisEvaluationQueryTimeout(queryExecutionProgress: QueryExecutionProgress)
    extends AnalysisEvaluationResult

case class AnalysisEvaluationPluginTimeout(analysisEvaluationProgress: AnalysisEvaluationProgress)
    extends AnalysisEvaluationResult

object AnalysisEvaluationStopped extends AnalysisEvaluationResult
