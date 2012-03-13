package cz.payola.common.model

object SharePrivilege
{
    val ResultOnly: Int = 1 << 0

    val IncludingData: Int = 1 << 1
}

trait AnalysisShare extends Entity
{
    /** Type of the analysis that is being share. */
    type AnalysisType <: Analysis

    def analysis: Analysis

    var privilege: Int
}
