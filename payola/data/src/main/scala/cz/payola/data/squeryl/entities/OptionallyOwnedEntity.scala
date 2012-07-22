package cz.payola.data.squeryl.entities

import cz.payola.domain.Entity

trait OptionallyOwnedEntity extends cz.payola.domain.entities.OptionallyOwnedEntity
{
    self: Entity =>
    
    var ownerId = owner.map(_.id)

    /**
      * @param value New owner of entity
      */
    def owner_=(value: Option[User]) {
        ownerId = value.map(_.id)

        _owner = value
    }
}
