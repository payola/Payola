package cz.payola.data.entities

import cz.payola.data.PayolaDB
import org.squeryl.annotations.Transient

/**
  * This object converts [[cz.payola.common.entities.Group]] to [[cz.payola.data.entities.Group]]
  */
object Group {

    def apply(g: cz.payola.common.entities.Group): Group = {
        g match {
            case group: Group => group
            case _ => new Group(g.id, g.name, User(g.owner))
        }
    }
}

class Group(
    override val id: String,
    name: String,
    o: User)
    extends cz.payola.domain.entities.Group(name, o)
    with PersistableEntity
{
    val ownerId: Option[String] = if (owner == null) None else Some(owner.id)

    private lazy val _ownerQuery = PayolaDB.groupOwnership.right(this)

    @Transient
    private var _groupMembersLoaded = false
    private lazy val _groupMembersQuery = PayolaDB.groupMembership.right(this)

    override def members: collection.Seq[UserType] = {
        if (!_groupMembersLoaded) {
            evaluateCollection(_groupMembersQuery).map(u =>
                if (!super.members.contains(u)) {
                    super.storeMember(u)
                }
            )

            _groupMembersLoaded = true
        }

        super.members
    }

    override def owner: UserType = {
        if (_owner == null && ownerId != null){
            _owner = evaluateCollection(_ownerQuery)(0)
        }

        _owner
    }

    override def storeMember(u: UserType) {
        super.storeMember(associate(User(u), _groupMembersQuery))
    }

    override protected def discardMember(user: UserType) {
        super.discardMember(dissociate(User(user), _groupMembersQuery))
    }
}
