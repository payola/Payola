package cz.payola.data.entities

import schema.PayolaDB

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
            associate(u.asInstanceOf[User], _groupMembersQuery)
        }
    }

    override def removeMember(u: cz.payola.domain.entities.User) = {
        super.removeMember(u)

        if (u.isInstanceOf[User]) {
            dissociate(u.asInstanceOf[User], _groupMembersQuery)
        }
    }
}
