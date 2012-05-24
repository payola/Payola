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

    override def addOwnedAnalysis(a: AnalysisType) {
        super.addOwnedAnalysis(
            a match {
                // Just associate Analysis with user and persist
                case analysis: Analysis => {
                    associate(analysis, _ownedAnalysesQuery);

                    analysis
                }
                // "Convert" to data.Analysis, associate with user and persist
                case analysis: cz.payola.domain.entities.Analysis => {
                    // TODO: maybe also "converting" parameter values
                    val an = new Analysis(analysis.name, None)
                    associate(an, _ownedAnalysesQuery)

                    an
                }
            }
        )
    }

    override def addOwnedGroup(g: GroupType) = {
        super.addOwnedGroup(
            g match {
                // Just associate Group with user and persist
                case group: Group => {
                    associate(group, _ownedGroupsQuery);

                    group
                }
                // "Convert" to data.Group, associate with user and persist
                case group: cz.payola.domain.entities.Group => {
                    // TODO: maybe also "converting" parameter values
                    val gr = new Group(group.name, this)
                    associate(gr, _ownedGroupsQuery)

                    gr
                }
            }
        )
    }
}
