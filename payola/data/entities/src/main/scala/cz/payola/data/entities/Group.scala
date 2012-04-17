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
    with PersistableEntity
{
    val ownerId: String = if (owner == null) "" else owner.id

    private lazy val _groupMembersQuery = PayolaDB.groupMembership.right(this)

    override def members : collection.Seq[UserType] = {
        evaluateCollection(_groupMembersQuery)
    }

    override def addMember(u: cz.payola.domain.entities.User) = {
        super.addMember(u)

        if (u.isInstanceOf[User]) {
            transaction {
                if (_groupMembersQuery.find(user => u.id == user.id) == None) {
                    _groupMembersQuery.associate(u.asInstanceOf[User])
                }
            }
        }
    }

    override def removeMember(u: cz.payola.domain.entities.User) = {
        super.removeMember(u)

        if (u.isInstanceOf[User]) {
            transaction {
                if (_groupMembersQuery.find(user => u.id == user.id) != None) {
                    _groupMembersQuery.dissociate(u.asInstanceOf[User])
                }
            }
        }
    }
}
