package cz.payola.domain.entities

import scala.collection._
import cz.payola.common._
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.settings._
import cz.payola.domain.DomainException

/**
  * @param _name Name of the user.
  */
class User(protected var _name: String)
    extends cz.payola.domain.Entity
    with NamedEntity
    with PrivilegeableEntity
    with cz.payola.common.entities.User
{
    checkConstructorPostConditions()

    type GroupType = Group

    type AnalysisType = Analysis

    type DataSourceType = DataSource

    type PluginType = Plugin

    type CustomizationType = Customization

    type PrefixType = Prefix

    type AnalysisResultType = AnalysisResult

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
    
    /**
      * Adds the prefix to the users prefixes. The prefix has to be owned by the user.
      * @param prefix The prefix to be added.
      */
    def addOwnedPrefix(prefix: PrefixType) {
        addOwnedEntity(prefix, availablePrefixes, storeOwnedPrefix)
    }

    /**
      * Removes the specified prefix from the users owned prefixes.
      * @param prefix The prefix to be removed.
      * @return The removed prefix.
      */
    def removeOwnedPrefix(prefix: PrefixType): Option[PrefixType] = {
        removeRelatedEntity(prefix, availablePrefixes, discardOwnedPrefix)
    }

    /**
     * Removes the specified prefix from the users owned prefixes.
     * @param prefix The prefix to be removed.
     * @return The removed prefix.
     */
    def removeOwnedAnalysisResult(analysisResult: AnalysisResultType): Option[AnalysisResultType] = {
        removeRelatedEntity(analysisResult, availableAnalysesResults, discardStoredAnalysisResult)
    }

    /**
     * Returns the users owned entities of the specified class.
     */
    def getOwnedEntities(entityClassName: String): Seq[Entity with NamedEntity] = {
        Map(
            Entity.getClassName(classOf[Analysis]) -> ownedAnalyses,
            Entity.getClassName(classOf[DataSource]) -> ownedDataSources,
            Entity.getClassName(classOf[Plugin]) -> ownedPlugins,
            Entity.getClassName(classOf[Customization]) -> ownedCustomizations,
            Entity.getClassName(classOf[Group]) -> ownedGroups,
            Entity.getClassName(classOf[Prefix]) -> availablePrefixes,
            Entity.getClassName(classOf[AnalysisResult]) -> availableAnalysesResults
        ).getOrElse(entityClassName, throw new DomainException("The user doesn't own entities of class " +
            entityClassName + "."))
    }

    /**
     * Returns the users owned entities of the specified class.
     */
    def getOwnedEntities(entityClass: Class[_]): Seq[Entity with NamedEntity] = {
        getOwnedEntities(Entity.getClassName(entityClass))
    }

    /**
     * Returns the users owned entity with the specified class and id.
     */
    def getOwnedEntity(entityClassName: String, entityId: String): Option[Entity with NamedEntity] = {
        getOwnedEntities(entityClassName).find(_.id == entityId)
    }

    /**
     * Returns the users owned entity with the specified class and id.
     */
    def getOwnedEntity(entityClass: Class[_], entityId: String): Option[Entity with NamedEntity] = {
        getOwnedEntity(entityClass.getName, entityId)
    }

    /**
     * Returns the users owned shareable entity with the specified class and id.
     */
    def getOwnedShareableEntity(entityClassName: String, entityId: String): Option[ShareableEntity] = {
        getOwnedEntity(entityClassName, entityId).flatMap {
            case p: ShareableEntity => Some(p)
            case _ => None
        }
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[User]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }

    override def email_=(value: String) {
        val emailRegEx = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$"
        validate(value.length == 0 || value.matches(emailRegEx), "email", "This is not valid email address.")

        super.email = value
    }
}
