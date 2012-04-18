package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Group(
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Group(name, owner)
    with KeyedEntity[String]
    with PersistableEntity
{
    val ownerId: String = if (owner == null) "" else owner.id

    lazy val members2 = PayolaDB.groupMembership.right(this)
}
