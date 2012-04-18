package cz.payola.common.entities

import permissions.privilege.Privilege
import scala.collection
import scala.collection.mutable

trait User extends NamedEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    /** Type of the data sources that the user can own. */
    type DataSourceType <: DataSource

    /** Type of the privileges. */
    type PrivilegeType <: Privilege[_,_]

    protected var _email: String

    protected var _password: String

    protected val _ownedGroups: mutable.Seq[GroupType]

    protected val _ownedAnalyses: mutable.Seq[AnalysisType]

    protected val _ownedDataSources: mutable.Seq[DataSourceType]
    
    protected val _privileges: mutable.Seq[PrivilegeType]

    def email = _email

    def email_=(value: String) {
        _email = value
    }

    def password = _password

    def password_=(value: String) {
        _password = value
    }
    
    def ownedGroups: collection.Seq[GroupType] = _ownedGroups

    def ownedAnalyses: collection.Seq[AnalysisType] = _ownedAnalyses

    def ownedDataSources: collection.Seq[DataSource] = _ownedDataSources
    
    def privileges: collection.Seq[PrivilegeType] = _privileges
}
