package cz.payola.data.entities

import org.squeryl.KeyedEntity
import schema.PayolaDB
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer

class Group(
        id: String,
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Group(id, name, owner)
    with KeyedEntity[String]
{
    val ownerId: String = if (owner == null) "" else owner.id

    private lazy val _groupMembers2 = PayolaDB.groupMembership.right(this)
    
    def groupMembers2 : Seq[User]= {
        transaction {
            val users: ArrayBuffer[User] = new ArrayBuffer[User]()

            for (u <- _groupMembers2) {
                users += u
            }

            users.toSeq
        }
    }

    def addMember(user: User) = {
        transaction {
            _groupMembers2.associate(user)
        }
    }
}
