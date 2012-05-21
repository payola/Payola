package cz.payola.common.entities

import permissions.privilege.Privilege
import scala.collection.mutable
import cz.payola.common.entities.analyses.DataSource

/**
  * A user of the application.
  */
trait User extends NamedEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    /** Type of the data sources that the user can own. */
    type DataSourceType <: DataSource

    /** Type of the privileges. */
    type PrivilegeType <: Privilege[_]

    protected var _email: String

    protected var _password: String

    protected val _ownedGroups: mutable.Seq[GroupType]

    protected val _ownedAnalyses: mutable.Seq[AnalysisType]

    protected val _ownedDataSources: mutable.Seq[DataSourceType]

    protected val _privileges: mutable.Seq[PrivilegeType]

    /** Email of the user. */
    def email = _email

    /**
      * Sets the email of the user.
      * @param value The new value of the users email.
      */
    def email_=(value: String) {
        _email = value
    }

    /** Password of the user required when logging into the application. */
    def password = _password

    /**
      * Sets the password of the user.
      * @param value The new value of the password.
      */
    def password_=(value: String) {
        _password = value
    }

    /** The groups that are owned by the user. */
    def ownedGroups: Seq[GroupType] = _ownedGroups

    /** The analyses that are owned by the user. */
    def ownedAnalyses: Seq[AnalysisType] = _ownedAnalyses

    /** The data sources that are owned by the user. */
    def ownedDataSources: Seq[DataSource] = _ownedDataSources

    /** Privileges of the user. */
    def privileges: Seq[PrivilegeType] = _privileges
}
