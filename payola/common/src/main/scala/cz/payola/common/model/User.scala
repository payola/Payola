package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

trait User extends NamedEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    protected var _email: String

    protected var _password: String

    protected val _ownedGroups: mutable.Seq[GroupType]

    protected val _ownedAnalyses: mutable.Seq[AnalysisType]

    def email = _email

    def email_=(value: String) {
        _email = value
    }

    def password = _password

    def password_=(value: String) {
        _password = value
    }

    def ownedGroups: immutable.Seq[GroupType] = _ownedGroups.toList

    def ownedAnalyses: immutable.Seq[AnalysisType] = _ownedAnalyses.toList
}
