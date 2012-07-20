package cz.payola.domain.entities

import cz.payola.domain.Entity

trait OptionallyOwnedEntity extends cz.payola.common.entities.OptionallyOwnedEntity
{
    self: Entity =>

    type UserType = User

    protected def checkInvariants() {
        validate(owner != null, "owner", "Owner of the %s mustn't be null.".format(entityTypeName))
    }
}
