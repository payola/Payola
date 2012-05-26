package cz.payola.data.entities

import cz.payola.data.PayolaDB

object Group {

    def apply(g: cz.payola.common.entities.Group): Group = {
        new Group(g.id, g.name, User(g.owner))
    }
}

class Group(
    override val id: String,
    name: String,
    owner: User)
    extends cz.payola.domain.entities.Group(name, owner)
    with PersistableEntity
{
    val ownerId: Option[String] = if (owner == null) None else Some(owner.id)

    private lazy val _groupMembersQuery = PayolaDB.groupMembership.right(this)

    override def members: collection.Seq[UserType] = {
        evaluateCollection(_groupMembersQuery)
    }

    override def addMember(u: UserType) {
        super.addMember(
            u match {
                // Just associate User with group
                case user: User => {
                    associate(user, _groupMembersQuery);

                    user
                }
                // "Convert" to data.User, associate with group and persist
                case user: cz.payola.domain.entities.User => {
                    val usr = new User(user.id, user.name, user.password, user.email)
                    associate(usr, _groupMembersQuery)

                    usr
                }
            }
        )
    }

    override protected def discardMember(user: UserType) {
        user match {
            case u: User => dissociate(u, _groupMembersQuery)
            case _ =>
        }
    }
}
