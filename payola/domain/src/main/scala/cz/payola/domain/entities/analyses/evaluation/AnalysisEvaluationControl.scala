package cz.payola.domain.entities.analyses.evaluation

/**
  * A message used to control an analysis evaluation.
  */
private[evaluation] abstract class AnalysisEvaluationControl

/**
  * Signal to stop the analysis evaluation.
  */
private[evaluation] object Stop extends AnalysisEvaluationControl

/**
  * Signal to terminate the analysis evaluation.
  */
private[evaluation] object Terminate extends AnalysisEvaluationControl

/**
  * Signal to respond with the analysis evaluation progress.
  */
private[evaluation] object GetProgress extends AnalysisEvaluationControl

/**
  * Signal to respond with the analysis evaluation result.
  */
private[evaluation] object GetResult extends AnalysisEvaluationControl
