package cz.payola.data.entities

class User(name: String, pwd: String, email: String)
    extends cz.payola.domain.entities.User(name) with PersistableEntity
{
    password_=(pwd)
    email_=(email)

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

    def addToGroup(g: GroupType) {
        //TODO: super.addToGroup(g);

        if (g.isInstanceOf[Group]) {
            associate(g.asInstanceOf[Group], _memberGroupsQuery)
        }
    }

    def removeFromGroup(g: GroupType) {
        // TODO: super.removeFromGroup(g)

        if (g.isInstanceOf[Group]) {
            dissociate(g.asInstanceOf[Group], _memberGroupsQuery)
        }
    }


    /* TODO: how to handle managing owned entities (depends on field owner on entity)
    override def removeOwnedGroup(a: AnalysisType) = null

    override def removeOwnedGroup(g: cz.payola.domain.entities.Group) {}

    override def addAnalysis(a: cz.payola.domain.entities.AnalysisType) = null

    override def addOwnedGroup(g: cz.payola.domain.entities.Group) = null
    */
}
