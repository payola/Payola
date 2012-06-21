package cz.payola.common.entities

import permissions.privilege.Privilege
import scala.collection.mutable
import cz.payola.common.entities.plugins.DataSource

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

    type OntologyCustomizationType <: settings.ontology.Customization

    /** Type of the privileges. */
    type PrivilegeType <: Privilege[_]

    protected var _email: String = ""

    protected var _password: String = ""

    protected val _ownedGroups = mutable.ArrayBuffer[GroupType]()

    protected val _ownedAnalyses = mutable.ArrayBuffer[AnalysisType]()

    protected val _ownedDataSources = mutable.ArrayBuffer[DataSourceType]()

    protected val _ontologyCustomizations = mutable.ArrayBuffer[OntologyCustomizationType]()

    protected val _privileges = mutable.ArrayBuffer[PrivilegeType]()

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

    /** Ontology customizations of the user. */
    def ownedOntologyCustomizations: Seq[OntologyCustomizationType] = _ontologyCustomizations

    /** Privileges of the user. */
    def privileges: Seq[PrivilegeType] = _privileges

    /**
      * Stores the specified analysis to the users owned analyses.
      * @param analysis The analysis to store.
      */
    protected def storeOwnedAnalysis(analysis: AnalysisType) {
        _ownedAnalyses += analysis
    }

    /**
      * Discards the specified analysis from the users owned analyses. Complementary operation to store.
      * @param analysis The analysis to discard.
      */
    protected def discardOwnedAnalysis(analysis: AnalysisType) {
        _ownedAnalyses -= analysis
    }

    /**
      * Stores the specified group to the users owned groups.
      * @param group The group to store.
      */
    protected def storeOwnedGroup(group: GroupType) {
        _ownedGroups += group
    }

    /**
      * Discards the specified group from the users owned groups. Complementary operation to store.
      * @param group The group to discard.
      */
    protected def discardOwnedGroup(group: GroupType) {
        _ownedGroups -= group
    }

    /**
      * Stores the specified data source to the users owned data sources.
      * @param source The data source to store.
      */
    protected def storeOwnedDataSource(source: DataSourceType) {
        _ownedDataSources += source
    }

    /**
      * Discards the specified data source from the users owned data sources. Complementary operation to store.
      * @param source The data source to discard.
      */
    protected def discardOwnedDataSource(source: DataSourceType) {
        _ownedDataSources -= source
    }

    /**
      * Stores the specified privileges to the users.
      * @param privilege The privilege to store.
      */
    protected def storePrivilege(privilege: PrivilegeType) {
        _privileges += privilege
    }

    /**
      * Discards the privileges from the user. Complementary operation to store.
      * @param privilege The privilege to discard.
      */
    protected def discardPrivilege(privilege: PrivilegeType) {
        _privileges -= privilege
    }

    /**
      * Stores the specified customization to the users.
      * @param customization The customization to store.
      */
    protected def storeOntologyCustomization(customization: OntologyCustomizationType) {
        _ontologyCustomizations += customization
    }

    /**
      * Discards the customization from the user. Complementary operation to store.
      * @param customization The customization to discard.
      */
    protected def discardOntologyCustomization(customization:OntologyCustomizationType) {
        _ontologyCustomizations -= customization
    }


}
