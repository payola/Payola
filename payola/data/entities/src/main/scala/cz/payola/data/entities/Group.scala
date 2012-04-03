package cz.payola.data.entities

/*
class Group(id: String = "", name: String = "", owner: User = null)
    extends cz.payola.domain.entities.Group(name, owner)
{
    override val _id: String = id

    //TODO: owner can be null when creating instance without parameters
    override val _ownerID: String = if (owner == null) "" else owner.id

    def ownerId: String = _ownerID
}
*/
class Group(val id: String = "", val name: String = "", val owner: User = null)
{
    val ownerId: String = if (owner == null) "" else owner.id
}
