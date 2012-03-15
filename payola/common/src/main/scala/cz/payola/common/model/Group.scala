package cz.payola.common.model

trait Group extends NamedEntity with OwnedEntity
{
    /** Type of the analysis shares that are associated with the group. */
    type AnalysisShareType <: AnalysisShare

    protected val _members: Seq[UserType]

    protected val _sharedAnalyses: Seq[AnalysisShareType]

    def members = _members

    def sharedAnalyses = _sharedAnalyses
}
