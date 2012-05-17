package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.PluginInstance

abstract class AnalysisResult

case class Error(error: Throwable, instanceErrors: Map[PluginInstance, Throwable]) extends AnalysisResult

case class Success(outputGraph: Graph, instanceErrors: Map[PluginInstance, Throwable]) extends AnalysisResult

object Timeout extends AnalysisResult

object Stopped extends AnalysisResult
