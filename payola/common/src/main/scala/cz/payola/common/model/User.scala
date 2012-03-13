package cz.payola.common.model

trait User extends NamedEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    /** Type of the analysis shares that are associated with the user. */
    type AnalysisShareType <: AnalysisShare

    var email: String

    var password: String

    def ownedGroups: Seq[GroupType]

    def memberGroups: Seq[GroupType]

    def ownedAnalyses: Seq[AnalysisType]

    def sharedAnalyses: Seq[AnalysisShareType]
}
