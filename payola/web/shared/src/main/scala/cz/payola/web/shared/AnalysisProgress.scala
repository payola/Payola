package cz.payola.web.shared

import cz.payola.common.rdf.Graph

class AnalysisProgress(val evaluated: Seq[String], val running: Seq[String], val errors: Seq[String],
    val percent: Double, val isFinished: Boolean, val graph: Option[Graph])
{
}
