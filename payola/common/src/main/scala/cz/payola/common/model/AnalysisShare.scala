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

    protected val _analysis: Analysis

    protected var _privilege: Int

    def analysis = _analysis

    def privilege = _privilege

    def privilege_=(value: Int) {
        _privilege = value
    }
}
