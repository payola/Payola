package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Analysis(name: String, owner: Option[User])
    extends cz.payola.domain.entities.Analysis(name, owner) with KeyedEntity[String] with PersistableEntity
{
    val ownerId: Option[String] = owner.map(_.id)
}
