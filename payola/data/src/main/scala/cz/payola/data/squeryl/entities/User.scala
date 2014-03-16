package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.plugins.DataSource
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._

/**
 * This object converts [[cz.payola.common.entities.User]] to [[cz.payola.common.entities.User]].
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

/**
 * Provides database persistence to [[cz.payola.domain.entities.User]] entity.
 * @param id ID of the user
 * @param name Name of the user
 * @param pwd Password of the user
 * @param mail Email of the user
 * @param context Implicit context
 */
class User(override val id: String, name: String, pwd: String, mail: String)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.User(name)
    with Entity with PrivilegeableEntity
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

    _customizations = null

    private lazy val _ownedCustomizationsQuery = context.schema.customizationOwnership.left(this)

    _availablePrefixes = null

    private lazy val _ownedPrefixesQuery = context.schema.prefixOwnership.left(this)

    _availableAnalysesResults = null

    private lazy val _storedAnalysesResults = context.schema.storedAnalysisResultOwnership.left(this)

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

    override def ownedCustomizations: immutable.Seq[CustomizationType] = {
        if (_customizations == null || _customizations.isEmpty) {

            wrapInTransaction {
                _customizations = mutable.ArrayBuffer(
                    context.customizationRepository.getAllByOwnerId(Some(id)): _*
                )
            }
        }
        _customizations.toList
    }

    override def availablePrefixes: immutable.Seq[PrefixType] = {
        if (_availablePrefixes == null) {
            wrapInTransaction {
                _availablePrefixes = mutable.ArrayBuffer(
                    context.prefixRepository.getAllAvailableToUser(Some(id)): _*
                )
            }
        }

        _availablePrefixes.toList
    }

    override def availableAnalysesResults: immutable.Seq[AnalysisResultType] = {
        if (_availableAnalysesResults == null) {
            wrapInTransaction {
                _availableAnalysesResults = mutable.ArrayBuffer(
                    context.analysisResultRepository.getAllAvailableToUser(Some(id)): _*
                )
            }
        }

        _availableAnalysesResults.toList
    }

    override protected def storeOwnedAnalysis(analysis: User#AnalysisType) {
        super.storeOwnedAnalysis(context.schema.associate(Analysis(analysis), _ownedAnalysesQuery))
    }

    override protected def storeOwnedGroup(group: User#GroupType) {
        super.storeOwnedGroup(context.schema.associate(Group(group), _ownedGroupsQuery))
    }

    override protected def storeOwnedDataSource(source: User#DataSourceType) {
        super.storeOwnedDataSource(context.schema.associate(DataSource(source), _ownedDataSourcesQuery))
    }

    override protected def storeOwnedPlugin(plugin: User#PluginType) {
        context.schema.associate(PluginDbRepresentation(plugin), _ownedPluginsQuery)

        super.storeOwnedPlugin(plugin)
    }

    override protected def storeCustomization(customization: User#CustomizationType) {
        super.storeCustomization(
            context.schema.associate(Customization(customization), _ownedCustomizationsQuery)
        )
    }

    override protected def storeOwnedPrefix(prefix: User#PrefixType) {
        if (prefix.owner == Some(this))
            context.schema.associate(Prefix(prefix), _ownedPrefixesQuery)

        super.storeOwnedPrefix(prefix)
    }

    override protected def storeAnalysisResult(analysisResult: User#AnalysisResultType) {
        if (analysisResult.owner == Some(this))
            context.schema.associate(AnalysisResult(analysisResult), _storedAnalysesResults)

        super.storeAnalysisResult(analysisResult)
    }

    override protected def discardOwnedAnalysis(analysis: User#AnalysisType) {
        context.analysisRepository.removeById(analysis.id)
        super.discardOwnedAnalysis(analysis)
    }

    override protected def discardOwnedGroup(group: User#GroupType) {
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

    override protected def discardCustomization(customization: User#CustomizationType) {
        context.customizationRepository.removeById(customization.id)

        super.discardCustomization(customization)
    }

    override protected def discardOwnedPrefix(prefix: User#PrefixType) {
        if (prefix.owner == Some(this))
            context.prefixRepository.removeById(prefix.id)

        super.discardOwnedPrefix(prefix)
    }

    override protected def discardStoredAnalysisResult(analysisResult: User#AnalysisResultType) {
        context.analysisResultRepository.removeById(analysisResult.id)
        super.discardStoredAnalysisResult(analysisResult)
    }
}
