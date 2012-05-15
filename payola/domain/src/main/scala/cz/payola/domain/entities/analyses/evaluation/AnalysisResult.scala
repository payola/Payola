package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.{PluginInstance, AnalysisException}

abstract class AnalysisResult

case class Error(exception: AnalysisException) extends AnalysisResult

case class InstanceError(instance: PluginInstance, throwable: Throwable) extends AnalysisResult

case class Success(outputGraph: Graph) extends AnalysisResult

object Timeout extends AnalysisResult

object Stopped extends AnalysisResult
