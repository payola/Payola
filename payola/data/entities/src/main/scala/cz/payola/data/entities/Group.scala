package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Group(
        id: String,
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Group(id, name, owner)
    with KeyedEntity[String]
    with PersistableEntity
{
    val ownerId: String = if (owner == null) "" else owner.id

    lazy val members2 = PayolaDB.groupMembership.right(this)
}
