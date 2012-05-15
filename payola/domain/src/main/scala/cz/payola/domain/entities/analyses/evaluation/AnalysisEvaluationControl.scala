package cz.payola.domain.entities.analyses.evaluation

abstract class AnalysisEvaluationControl

object Stop extends AnalysisEvaluationControl

object Terminate extends AnalysisEvaluationControl

object GetProgress extends AnalysisEvaluationControl

object GetResult extends AnalysisEvaluationControl
