package cz.payola.data.entities

import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient
import cz.payola.data.dao._
import cz.payola.data.entities.analyses.DataSource

object User {

    def apply(u: cz.payola.common.entities.User): User = {
        u match {
            case user : User => user
            case _ => new User(u.id, u.name, u.password, u.email)
        }
    }
}

class User(
    override val id: String,
    name: String,
    pwd: String,
    mail: String)
    extends cz.payola.domain.entities.User(name) with PersistableEntity
{
    password_=(pwd)
    email_=(mail)

    @Transient
    private var _ownedGroupsLoaded = false
    private lazy val _ownedGroupsQuery = PayolaDB.groupOwnership.left(this)

    @Transient
    private var _ownedAnalysesLoaded = false
    private lazy val _ownedAnalysesQuery = PayolaDB.analysisOwnership.left(this)

    @Transient
    private var _ownedDataSourcesLoaded = false
    private lazy val _ownedDataSourcesQuery = PayolaDB.dataSourceOwnership.left(this)

    @Transient
    private var _memberGroupsLoaded = false
    private lazy val _memberGroupsQuery = PayolaDB.groupMembership.left(this)

    override def ownedGroups: collection.Seq[GroupType] = {
        if (!_ownedGroupsLoaded) {
            evaluateCollection(_ownedGroupsQuery).map(g => 
                if (!super.ownedGroups.contains(g))
                    super.storeOwnedGroup(g)
            )

            _ownedGroupsLoaded = true
        }

        super.ownedGroups
    }

    override def ownedAnalyses: collection.Seq[AnalysisType] = {
        if (!_ownedAnalysesLoaded) {
            evaluateCollection(_ownedAnalysesQuery).map(a => 
                if (!super.ownedAnalyses.contains(a))
                    super.storeOwnedAnalysis(a)
            )

            _ownedAnalysesLoaded = true
        }

        super.ownedAnalyses
    }

    override def ownedDataSources: collection.Seq[DataSourceType] = {
        if (!_ownedDataSourcesLoaded) {
            evaluateCollection(_ownedDataSourcesQuery).map(ds =>
                if (!super.ownedDataSources.contains(ds))
                    super.storeOwnedDataSource(ds)
            )

            _ownedDataSourcesLoaded = true
        }

        super.ownedDataSources
    }

    override protected def storeOwnedAnalysis(analysis: User#AnalysisType) {
        super.storeOwnedAnalysis(associate(Analysis(analysis), _ownedAnalysesQuery).get)
    }

    override protected def storeOwnedGroup(group: User#GroupType) {
        super.storeOwnedGroup(associate(Group(group), _ownedGroupsQuery).get)
    }

    override protected def storeOwnedDataSource(source: User#DataSourceType) {
        super.storeOwnedDataSource(associate(DataSource(source), _ownedDataSourcesQuery).get)
    }

    override protected def discardOwnedAnalysis(analysis: User#AnalysisType) {
        // TODO: injection
        new AnalysisDAO().removeById(analysis.id)

        super.discardOwnedAnalysis(analysis)
    }

    override protected def discardOwnedGroup(group: User#GroupType)  {
        // TODO: injection
        new GroupDAO().removeById(group.id)
        
        super.discardOwnedGroup(group)
    }

    override protected def discardOwnedDataSource(source: User#DataSourceType) {
        // TODO: injection
        new DataSourceDAO().removeById(source.id)

        super.discardOwnedDataSource(source)
    }

    //TODO: Privileges...
    // override protected def storePrivilege(privilege: User#PrivilegeType) = null

    //TODO: Privileges...
    // override protected def discardPrivilege(privilege: User#PrivilegeType) = null
}
