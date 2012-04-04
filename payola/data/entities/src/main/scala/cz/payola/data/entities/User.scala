package cz.payola.data.entities

import org.squeryl.dsl.OneToMany
import org.squeryl.KeyedEntity

class User(
        id: String,
        name: String,
        pwd: String,
        email: String)
    extends cz.payola.domain.entities.User(id, name)
    with KeyedEntity[String]
{
    password_=(pwd)
    email_=(email)

    lazy val ownedGroups2: OneToMany[Group] = PayolaDB.groupOwnership.left(this)

    lazy val memberedGroups = PayolaDB.groupMembership.left(this)
}