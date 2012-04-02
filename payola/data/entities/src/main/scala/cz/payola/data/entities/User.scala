package cz.payola.data.entities

import java.util.UUID
import org.squeryl.dsl.OneToMany
import org.squeryl.KeyedEntity
import tools.nsc.util.TableDef.Column

/*
class User(id: String = "", name: String = "", pwd: String = "", email: String = "")
    extends cz.payola.domain.entities.User(name)
    with KeyedEntity[String]
{
    override val _id: String = id
    _password = pwd
    _email = email

    lazy val _ownedGroups2: OneToMany[Group] =
        PayolaDB.groupOwners.left(this)

    def ownedGroups2 = _ownedGroups2    
}
*/
class User(
        val id: String,
        val name: String,
        val pwd: String,
        val email: String)
    extends KeyedEntity[String]
{
    lazy val _ownedGroups2: OneToMany[Group] =
        PayolaDB.groupOwners.left(this)

    def ownedGroups2 = _ownedGroups2
    //def id: String = id
}
