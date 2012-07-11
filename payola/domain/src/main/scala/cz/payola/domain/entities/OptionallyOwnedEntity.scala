package cz.payola.domain.entities

import cz.payola.domain.Entity

trait OptionallyOwnedEntity extends cz.payola.common.entities.OptionallyOwnedEntity
{
    self: Entity =>

    type UserType = User

    protected def checkInvariants() {
        require(owner != null, "Owner of the entity mustn't be null.")
    }
}
