package cz.payola.common.model

trait Group extends NamedEntity with OwnedEntity
{
    /** Type of the analysis shares that are associated with the group. */
    type AnalysisShareType <: AnalysisShare

    protected var _members: Seq[UserType]

    protected var _sharedAnalyses: Seq[AnalysisShareType]

    def members = _members

    def sharedAnalyses = _sharedAnalyses
}
