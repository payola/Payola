package cz.payola.data.entities

import org.squeryl.dsl.OneToMany
import org.squeryl.KeyedEntity

class User(
        i: String,
        name: String,
        pwd: String,
        email: String)
    extends cz.payola.domain.entities.User(name)
    with KeyedEntity[String]
{
    override val id: String = i
    _password = pwd
    _email = email

    lazy val ownedGroups2: OneToMany[Group] = PayolaDB.groupOwnership.left(this)

    lazy val memberedGroups = PayolaDB.groupMembership.left(this)
}