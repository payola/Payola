package cz.payola.common.model

trait Group extends NamedEntity with OwnedEntity
{
    /** Type of the analysis shares that are associated with the group. */
    type AnalysisShareType <: AnalysisShare

    def members: Seq[UserType]

    def sharedAnalyses: Seq[AnalysisShareType]
}
