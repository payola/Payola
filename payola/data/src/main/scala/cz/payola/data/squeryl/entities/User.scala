package cz.payola.data.squeryl.entities

import cz.payola.data._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.repositories._
import cz.payola.data.squeryl.entities.plugins.DataSource
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.User]] to [[cz.payola.common.entities.User]]
  */
object User extends EntityConverter[User]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[User] = {
        entity match {
            case e: User => Some(e)
            case e: cz.payola.common.entities.User => Some(new User(e.id, e.name, e.email, e.password))
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

    @Transient
    private var _ownedGroupsLoaded = false
    private lazy val _ownedGroupsQuery = context.schema.groupOwnership.left(this)

    @Transient
    private var _ownedAnalysesLoaded = false
    private lazy val _ownedAnalysesQuery = context.schema.analysisOwnership.left(this)

    @Transient
    private var _ownedPluginsLoaded = false
    private lazy val _ownedPluginsQuery = context.schema.pluginOwnership.left(this)

    @Transient
    private var _ownedDataSourcesLoaded = false
    private lazy val _ownedDataSourcesQuery = context.schema.dataSourceOwnership.left(this)

    @Transient
    private val _memberedGroups = new mutable.ArrayBuffer[Group]()
    private lazy val _memberedGroupsQuery = context.schema.groupMembership.left(this)

    override def ownedGroups: immutable.Seq[GroupType] = {
        if (!_ownedGroupsLoaded) {
            evaluateCollection(_ownedGroupsQuery).map(g => 
                if (!super.ownedGroups.contains(g)) {
                    super.storeOwnedGroup(g)
                }
            )

            _ownedGroupsLoaded = true
        }

        super.ownedGroups
    }

    /**
      * @return Returns collection of [[cz.payola.data.squeryl.entities.Group]]s that user is member of.
      */
    def memberedGroups: mutable.Seq[Group] = {
        if (_memberedGroups.size == 0) {
            // Lazy-load membered groups collection
            evaluateCollection(_memberedGroupsQuery).map(g =>
                _memberedGroups += g
            )
        }

        _memberedGroups
    }

    override def ownedAnalyses: immutable.Seq[AnalysisType] = {
        if (!_ownedAnalysesLoaded) {
            evaluateCollection(_ownedAnalysesQuery).map(a => 
                if (!super.ownedAnalyses.contains(a)) {
                    super.storeOwnedAnalysis(a)
                }
            )

            _ownedAnalysesLoaded = true
        }

        super.ownedAnalyses
    }

    override def ownedDataSources: immutable.Seq[DataSourceType] = {
        if (!_ownedDataSourcesLoaded) {
            evaluateCollection(_ownedDataSourcesQuery).map(ds =>
                if (!super.ownedDataSources.contains(ds)) {
                    super.storeOwnedDataSource(ds)
                }
            )

            _ownedDataSourcesLoaded = true
        }

        super.ownedDataSources
    }

    override def ownedPlugins: immutable.Seq[PluginType] = {
        if (!_ownedPluginsLoaded) {
            evaluateCollection(_ownedPluginsQuery).map(p => p.toPlugin).map(p =>
                if (!super.ownedPlugins.contains(p)) {
                    super.storeOwnedPlugin(p)
                }
            )

            _ownedPluginsLoaded = true
        }

        super.ownedPlugins
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
}
