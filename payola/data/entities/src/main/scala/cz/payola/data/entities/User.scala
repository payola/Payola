package cz.payola.data.entities

import org.squeryl.dsl.{OneToMany, ManyToMany}
import org.squeryl.KeyedEntity
import schema.PayolaDB
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer

class User(
        id: String,
        name: String,
        pwd: String,
        email: String)
    extends cz.payola.domain.entities.User(id, name)
    with KeyedEntity[String]
{
    password_=(pwd)
    email_=(email)

    private lazy val _ownedGroupsQuery: OneToMany[Group] = PayolaDB.groupOwnership.left(this)

    private lazy val _ownedAnalysesQuery: OneToMany[Analysis] = PayolaDB.analysisOwnership.left(this)

    private lazy val _memberGroupsQuery = PayolaDB.groupMembership.left(this)

    override def ownedGroups: ArrayBuffer[GroupType] = {
        transaction {
            val groups: ArrayBuffer[GroupType] = new ArrayBuffer[GroupType]()

            for (g <- _ownedGroupsQuery) {
                groups += g.asInstanceOf[Group]
            }

            groups
        }
    }

    override def ownedAnalyses: ArrayBuffer[AnalysisType] = {
        transaction {
            val analyses: ArrayBuffer[AnalysisType] = new ArrayBuffer[AnalysisType]()

            for (a <- _ownedAnalysesQuery) {
                analyses += a
            }

            analyses
        }
    }

    override def memberGroups: ArrayBuffer[GroupType]= {
        transaction {
            val groups: ArrayBuffer[GroupType] = new ArrayBuffer[GroupType]()

            for (g  <- _memberGroupsQuery) {
                groups += g
            }

            groups
        }
    }

    override def addToGroup(g: cz.payola.domain.entities.Group) = {
        super.addToGroup(g);

        if (g.isInstanceOf[Group]) {
            transaction {
                if (_memberGroupsQuery.find(group => g.id == group.id) == None) {
                    _memberGroupsQuery.associate(g.asInstanceOf[Group])
                }
            }
        }
    }

    override def removeFromGroup(g: cz.payola.domain.entities.Group) = {
        super.removeFromGroup(g)

        if (g.isInstanceOf[Group]) {
            transaction(
                if (_memberGroupsQuery.find(group => g.id == group.id) != None) {
                    _memberGroupsQuery.dissociate(g.asInstanceOf[Group])
                }
            )
        }
    }



    /* TODO: how to handle managing owned entities (depends on field owner on entity)
    override def removeOwnedGroup(a: AnalysisType) = null

    override def removeOwnedGroup(g: cz.payola.domain.entities.Group) {}

    override def addAnalysis(a: cz.payola.domain.entities.AnalysisType) = null

    override def addOwnedGroup(g: cz.payola.domain.entities.Group) = null
    */
}