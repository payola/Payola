package cz.payola.domain.entities.analyses.evaluation

import cz.payola.domain.rdf.Graph

case class InstanceEvaluationInput(index: Int, value: Option[Graph])
