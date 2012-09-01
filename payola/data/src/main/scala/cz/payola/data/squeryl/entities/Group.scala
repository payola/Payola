package cz.payola.data.squeryl.entities

import scala.collection._
import cz.payola.data.squeryl._

/**
 * This object converts [[cz.payola.common.entities.Group]] to [[cz.payola.data.squeryl.entities.Group]]
 */
object Group extends EntityConverter[Group]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Group] = {
        entity match {
            case e: Group => Some(e)
            case e: cz.payola.common.entities.Group => Some(new Group(e.id, e.name, User(e.owner)))
            case _ => None
        }
    }
}

class Group(override val id: String, name: String, o: User)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Group(name, o)
    with Entity with PrivilegeableEntity
{
    var ownerId: String = Option(o).map(_.id).getOrElse(null)

    def members_=(members: Seq[User]) {
        _members = mutable.ArrayBuffer(members: _*)
    }

    def owner_=(value: UserType) {
        _owner = value

        ownerId = value.id
    }

    override def storeMember(user: UserType) {
        super.storeMember(context.schema.associate(User(user), context.schema.groupMembership.right(this)))
    }

    override protected def discardMember(user: UserType) {
        super.discardMember(context.schema.dissociate(User(user), context.schema.groupMembership.right(this)))
    }
}
