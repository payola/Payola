package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.rdf.Graph

abstract class PluginEvaluationResult

case class PluginEvaluationSuccess(graph: Graph) extends PluginEvaluationResult

case class PluginEvaluationError(throwable: Throwable) extends PluginEvaluationResult
