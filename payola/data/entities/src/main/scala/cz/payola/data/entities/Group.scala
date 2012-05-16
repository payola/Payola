package cz.payola.data.entities

class Group(
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Group(name, owner)
    with PersistableEntity
{
    val ownerId: Option[String] = if (owner == null) None else Some(owner.id)

    private lazy val _groupMembersQuery = PayolaDB.groupMembership.right(this)

    override def members : collection.Seq[UserType] = {
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
                    val usr = new User(user.name, user.password, user.email)
                    associate(usr, _groupMembersQuery)

                    usr
                }
            }
        )
    }

    override def removeMember(u: UserType) {
        super.removeMember(u)

        if (u.isInstanceOf[User]) {
            dissociate(u.asInstanceOf[User], _groupMembersQuery)
        }
    }
}
