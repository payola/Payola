package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.plugins.DataSource
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.data.squeryl.entities.settings.OntologyCustomization

/**
  * This object converts [[cz.payola.common.entities.User]] to [[cz.payola.common.entities.User]]
  */
object User extends EntityConverter[User]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[User] = {
        entity match {
            case e: User => Some(e)
            case e: cz.payola.common.entities.User => Some(new User(e.id, e.name, e.password, e.email))
            case _ => None
        }
    }
}

class User(override val id: String, name: String, pwd: String, mail: String)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.User(name) with PersistableEntity with PrivilegableEntity
{
    password = pwd
    email = mail

    _ownedGroups = null
    private lazy val _ownedGroupsQuery = context.schema.groupOwnership.left(this)

    _ownedAnalyses = null
    private lazy val _ownedAnalysesQuery = context.schema.analysisOwnership.left(this)

    _ownedPlugins = null
    private lazy val _ownedPluginsQuery = context.schema.pluginOwnership.left(this)

    _ownedDataSources = null
    private lazy val _ownedDataSourcesQuery = context.schema.dataSourceOwnership.left(this)

    _ontologyCustomizations = null
    private lazy val _ownedCustomizationsQuery = context.schema.customizationOwnership.left(this)

    override def ownedGroups: immutable.Seq[GroupType] = {
        if (_ownedGroups == null) {
            wrapInTransaction {
                _ownedGroups = mutable.ArrayBuffer(
                    context.groupRepository.getAllByOwnerId(id): _*
                )
            }
        }

        _ownedGroups.toList
    }


    override def ownedAnalyses: immutable.Seq[AnalysisType] = {
        if (_ownedAnalyses == null) {
            wrapInTransaction {
                _ownedAnalyses = mutable.ArrayBuffer(
                    context.analysisRepository.getAllByOwnerId(Some(id)): _*
                )
            }
        }

        _ownedAnalyses.toList
    }


    override def ownedDataSources: immutable.Seq[DataSourceType] = {
        if (_ownedDataSources == null) {
            wrapInTransaction {
                _ownedDataSources = mutable.ArrayBuffer(
                    context.dataSourceRepository.getAllByOwnerId(Some(id)): _*
                )
            }
        }

        _ownedDataSources.toList
    }


    override def ownedPlugins: immutable.Seq[PluginType] = {
        if (_ownedPlugins == null) {
            wrapInTransaction {
                _ownedPlugins = mutable.ArrayBuffer(
                    context.pluginRepository.getAllByOwnerId(Some(id)): _*
                )
            }
        }

        _ownedPlugins.toList
    }


    override def ownedOntologyCustomizations: immutable.Seq[OntologyCustomizationType] = {
        if (_ontologyCustomizations == null) {
            wrapInTransaction {
                _ontologyCustomizations = mutable.ArrayBuffer(
                    context.ontologyCustomizationRepository.getAllByOwnerId(Some(id)): _*
                )
            }
        }

        _ontologyCustomizations.toList
    }

    override protected def storeOwnedAnalysis(analysis: User#AnalysisType) {
        super.storeOwnedAnalysis(associate(Analysis(analysis), _ownedAnalysesQuery))
    }

    override protected def storeOwnedGroup(group: User#GroupType) {
        super.storeOwnedGroup(associate(Group(group), _ownedGroupsQuery))
    }

    override protected def storeOwnedDataSource(source: User#DataSourceType) {
        super.storeOwnedDataSource(associate(DataSource(source), _ownedDataSourcesQuery))
    }

    override protected def storeOwnedPlugin(plugin: User#PluginType) {
        associate(PluginDbRepresentation(plugin), _ownedPluginsQuery)

        super.storeOwnedPlugin(plugin)
    }
    
    override protected def storeOntologyCustomization(customization: User#OntologyCustomizationType) {
        super.storeOntologyCustomization(associate(OntologyCustomization(customization), _ownedCustomizationsQuery))
    }

    override protected def discardOwnedAnalysis(analysis: User#AnalysisType) {
        context.analysisRepository.removeById(analysis.id)

        super.discardOwnedAnalysis(analysis)
    }

    override protected def discardOwnedGroup(group: User#GroupType)  {
        context.groupRepository.removeById(group.id)
        
        super.discardOwnedGroup(group)
    }

    override protected def discardOwnedDataSource(source: User#DataSourceType) {
        context.dataSourceRepository.removeById(source.id)

        super.discardOwnedDataSource(source)
    }
    
    override protected def discardOwnedPlugin(plugin: User#PluginType) {
        context.pluginRepository.removeById(plugin.id)

        super.discardOwnedPlugin(plugin)
    }
    

    override protected def discardOntologyCustomization(customization: User#OntologyCustomizationType) {
        context.ontologyCustomizationRepository.removeById(customization.id)

        super.discardOntologyCustomization(customization)
    }
    
}
