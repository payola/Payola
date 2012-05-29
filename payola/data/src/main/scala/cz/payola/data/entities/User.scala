package cz.payola.data.entities

import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient

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

    override protected def storeOwnedAnalysis(analysis: User#AnalysisType) {
        super.storeOwnedAnalysis(associate(Analysis(analysis), _ownedAnalysesQuery).get)
    }

    //TODO: override protected def discardOwnedAnalysis(analysis: User#AnalysisType) {}

    override protected def storeOwnedGroup(group: User#GroupType) {
        super.storeOwnedGroup(associate(Group(group), _ownedGroupsQuery).get)
    }

    //TODO: override protected def discardOwnedGroup(group: User#GroupType)  {}

    //TODO: override protected def storeOwnedDataSource(source: User#DataSourceType) = null

    //TODO: override protected def discardOwnedDataSource(source: User#DataSourceType) = null

    //TODO: override protected def storePrivilege(privilege: User#PrivilegeType) = null

    //TODO: override protected def discardPrivilege(privilege: User#PrivilegeType) = null
}
