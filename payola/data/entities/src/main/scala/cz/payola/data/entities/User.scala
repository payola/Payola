package cz.payola.data.entities

object User {

    def apply(u: cz.payola.common.entities.User): User = {
        new User(u.id, u.name, u.password, u.email)
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

    private lazy val _ownedGroupsQuery = PayolaDB.groupOwnership.left(this)

    private lazy val _ownedAnalysesQuery = PayolaDB.analysisOwnership.left(this)

    private lazy val _memberGroupsQuery = PayolaDB.groupMembership.left(this)

    override def ownedGroups: collection.Seq[GroupType] = {
        evaluateCollection(_ownedGroupsQuery)
    }

    override def ownedAnalyses: collection.Seq[AnalysisType] =  {
        evaluateCollection(_ownedAnalysesQuery)
    }

    override def memberGroups: collection.Seq[GroupType]= {
        evaluateCollection(_memberGroupsQuery)
    }

    override protected def storeOwnedAnalysis(analysis: User#AnalysisType) {
        analysis match {
            // Just associate Analysis with user and persist
            case a: Analysis => associate(a, _ownedAnalysesQuery)

            // "Convert" to data.Analysis, associate with user and persist
            case a: cz.payola.domain.entities.Analysis => associate(new Analysis(a.id, a.name, Some(this)), _ownedAnalysesQuery)
        }
    }

    //TODO: override protected def discardOwnedAnalysis(analysis: User#AnalysisType) {}

    override protected def storeOwnedGroup(group: User#GroupType)  {
        group match {
            // Just associate Group with user and persist
            case g: Group => associate(g, _ownedGroupsQuery);

            // "Convert" to data.Group, associate with user and persist
            case g: cz.payola.domain.entities.Group => associate(new Group(g.id, g.name, this), _ownedGroupsQuery)
        }
    }

    //TODO: override protected def discardOwnedGroup(group: User#GroupType)  {}

    //TODO: override protected def storeOwnedDataSource(source: User#DataSourceType) = null

    //TODO: override protected def discardOwnedDataSource(source: User#DataSourceType) = null

    //TODO: override protected def storePrivilege(privilege: User#PrivilegeType) = null

    //TODO: override protected def discardPrivilege(privilege: User#PrivilegeType) = null
}
