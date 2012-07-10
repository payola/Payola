package cz.payola.domain.entities

import scala.collection._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.privileges._
import cz.payola.domain.Entity
import cz.payola.domain.entities.settings.OntologyCustomization

/**
  * @param _name Name of the user.
  */
class User(protected var _name: String)
    extends Entity
    with NamedEntity
    with PrivilegableEntity
    with cz.payola.common.entities.User
{
    checkConstructorPostConditions()

    type GroupType = Group

    type AnalysisType = Analysis

    type DataSourceType = DataSource

    type PluginType = Plugin

    type OntologyCustomizationType = OntologyCustomization

    /**
      * Adds the analysis to the users owned analyses. The analysis has to be owned by the user.
      * @param analysis Analysis to be added.
      */
    def addOwnedAnalysis(analysis: AnalysisType) {
        addOwnedEntity(analysis, ownedAnalyses, storeOwnedAnalysis)
    }

    /**
      * Removes the specified analysis from the users owned analyses.
      * @param analysis The analysis to be removed.
      * @return The removed analysis.
      */
    def removeOwnedAnalysis(analysis: AnalysisType): Option[AnalysisType] = {
        removeRelatedEntity(analysis, ownedAnalyses, discardOwnedAnalysis)
    }

    /**
      * Adds the data source to the users owned data sources. The data source has to be owned by the user.
      * @param dataSource The data source to be added.
      */
    def addOwnedDataSource(dataSource: DataSourceType) {
        addOwnedEntity(dataSource, ownedDataSources, storeOwnedDataSource)
    }

    /**
      * Removes the specified data source from the users owned data sources.
      * @param dataSource The data source to be removed.
      * @return The removed data source.
      */
    def removeOwnedDataSource(dataSource: DataSourceType): Option[DataSourceType] = {
        removeRelatedEntity(dataSource, ownedDataSources, discardOwnedDataSource)
    }

    /**
      * Adds the plugin to the users owned plugins. The plugin has to be owned by the user.
      * @param plugin The plugin to be added.
      */
    def addOwnedPlugin(plugin: PluginType) {
        addOwnedEntity(plugin, ownedPlugins, storeOwnedPlugin)
    }

    /**
      * Removes the specified plugin from the users owned plugins.
      * @param plugin The plugin to be removed.
      * @return The removed plugin.
      */
    def removeOwnedPlugin(plugin: PluginType): Option[PluginType] = {
        removeRelatedEntity(plugin, ownedPlugins, discardOwnedPlugin)
    }

    /**
      * Adds the group to the users owned groups. The group has to be owned by the user.
      * @param group The group to be added.
      */
    def addOwnedGroup(group: GroupType) {
        addOwnedEntity(group, Option(group).flatMap(g => Option(g.owner)), ownedGroups, storeOwnedGroup)
    }

    /**
      * Removes the group from the users owned groups.
      * @param group The group to be removed.
      * @return The removed group.
      */
    def removeOwnedGroup(group: GroupType): Option[GroupType] = {
        removeRelatedEntity(group, ownedGroups, discardOwnedGroup)
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[User]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }
}
