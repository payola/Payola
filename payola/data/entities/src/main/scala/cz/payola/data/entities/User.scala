package cz.payola.data.entities

import org.squeryl.dsl.OneToMany
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

    private lazy val _ownedGroups2: OneToMany[Group] = PayolaDB.groupOwnership.left(this)

    private lazy val _ownedAnalyses2: OneToMany[Analysis] = PayolaDB.analysisOwnership.left(this)

    private lazy val _memberedGroups2 = PayolaDB.groupMembership.left(this)

    def ownedGroups2: Seq[Group]= {
        transaction {
            val groups: ArrayBuffer[Group] = new ArrayBuffer[Group]()

            for (g <- _ownedGroups2) {
                groups += g
            }

            groups.toSeq
        }
    }

    def memberedGroups2: Seq[Group]= {
        transaction {
            val groups: ArrayBuffer[Group] = new ArrayBuffer[Group]()

            for (g <- _memberedGroups2) {
                groups += g
            }

            groups.toSeq
        }
    }

    def ownedAnalyses2: Seq[Analysis]= {
        transaction {
            val analyses: ArrayBuffer[Analysis] = new ArrayBuffer[Analysis]()

            for (a <- _ownedAnalyses2) {
                analyses += a
            }

            analyses.toSeq
        }
    }

    def becomeMemberOf(group: Group) = {
        transaction {
            _memberedGroups2.associate(group)
        }
    }
}