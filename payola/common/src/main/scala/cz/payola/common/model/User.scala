package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

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

    protected val _ownedGroups: mutable.Seq[GroupType]

    protected val _memberGroups: mutable.Seq[GroupType]

    protected val _ownedAnalyses: mutable.Seq[AnalysisType]

    protected val _sharedAnalyses: mutable.Seq[AnalysisShareType]

    def email = _email

    def email_=(value: String) {
        _email = value
    }

    def password = _password

    def password_=(value: String) {
        _password = value
    }

    def ownedGroups: immutable.Seq[GroupType] = _ownedGroups

    def memberGroups: immutable.Seq[GroupType] = _memberGroups

    def ownedAnalyses: immutable.Seq[AnalysisType] = _ownedAnalyses

    def sharedAnalyses: immutable.Seq[AnalysisShareType] = _sharedAnalyses
}
