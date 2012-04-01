package cz.payola.data.entities

class Group(id: String, name: String, user:User)
    extends cz.payola.domain.entities.Group(name, user)
{
    override val _id: String = id
    override val _ownerID = user.id
}
