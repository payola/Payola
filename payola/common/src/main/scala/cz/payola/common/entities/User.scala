package cz.payola.common.entities

import scala.collection._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.common.entities.settings.OntologyCustomization

/**
  * An user of the application.
  */
trait User extends NamedEntity with PrivilegableEntity
{
    /** Type of the groups that the user can own or be member of. */
    type GroupType <: Group

    /** Type of the analyses that the user can own. */
    type AnalysisType <: Analysis

    /** Type of the data sources that the user can own. */
    type DataSourceType <: DataSource

    /** Type of the plugins that the user can own. */
    type PluginType <: Plugin

    /** Type of the ontology visual customizations that the user may own. */
    type OntologyCustomizationType <: OntologyCustomization

    protected var _email: String = ""

    protected var _password: String = ""

    protected val _ownedGroups = mutable.ArrayBuffer[GroupType]()

    protected val _ownedAnalyses = mutable.ArrayBuffer[AnalysisType]()

    protected val _ownedDataSources = mutable.ArrayBuffer[DataSourceType]()

    protected val _ownedPlugins = mutable.ArrayBuffer[PluginType]()

    protected val _ontologyCustomizations = mutable.ArrayBuffer[OntologyCustomizationType]()

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
    def ownedGroups: immutable.Seq[GroupType] = _ownedGroups.toList

    /** The analyses that are owned by the user. */
    def ownedAnalyses: immutable.Seq[AnalysisType] = _ownedAnalyses.toList

    /** The data sources that are owned by the user. */
    def ownedDataSources: immutable.Seq[DataSourceType] = _ownedDataSources.toList

    /** The plugins that are owned by the user. */
    def ownedPlugins: immutable.Seq[PluginType] = _ownedPlugins.toList

    /** Ontology customizations of the user. */
    def ownedOntologyCustomizations: immutable.Seq[OntologyCustomizationType] = _ontologyCustomizations.toList

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
      * Stores the specified plugin to the users owned plugins.
      * @param plugin The plugin to store.
      */
    protected def storeOwnedPlugin(plugin: PluginType) {
        _ownedPlugins += plugin
    }

    /**
      * Discards the specified plugin from the users owned plugins. Complementary operation to store.
      * @param plugin The plugin to discard.
      */
    protected def discardOwnedPlugin(plugin: PluginType) {
        _ownedPlugins -= plugin
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
    protected def discardOntologyCustomization(customization: OntologyCustomizationType) {
        _ontologyCustomizations -= customization
    }
}
