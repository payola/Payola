package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Analysis(
        id: String,
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Analysis(id, name, owner)
    with KeyedEntity[String]
{
    val ownerId: String = if (owner == null) "" else owner.id
}
