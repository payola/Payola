package cz.payola.common.model

import scala.collection.mutable.Seq

trait User extends NamedEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    /** Type of the analysis shares that are associated with the user. */
    type AnalysisShareType <: AnalysisShare

    protected var _email: String

    protected var _password: String

    protected val _ownedGroups: Seq[GroupType]

    protected val _memberGroups: Seq[GroupType]

    protected val _ownedAnalyses: Seq[AnalysisType]

    protected val _sharedAnalyses: Seq[AnalysisShareType]

    def email = _email

    def email_=(value: String) {
        _email = value
    }

    def password = _password

    def password_=(value: String) {
        _password = value
    }

    def ownedGroups = _ownedGroups

    def memberGroups = _memberGroups

    def ownedAnalyses = _ownedAnalyses

    def sharedAnalyses = _sharedAnalyses
}
