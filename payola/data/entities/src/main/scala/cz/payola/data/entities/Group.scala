package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Group(
        i: String,
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Group(name, owner)
    with KeyedEntity[String]
{
    override val id: String = i

    val ownerId: String = if (owner == null) "" else owner.id

    lazy val members2 = PayolaDB.groupMembership.right(this)
}
