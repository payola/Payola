package cz.payola.web.shared

class AnalysisProgress(val evaluated: Seq[String], val running: Iterable[String], val errors: Iterable[String],
    val percent: Double, val isFinished: Boolean)
{
}
