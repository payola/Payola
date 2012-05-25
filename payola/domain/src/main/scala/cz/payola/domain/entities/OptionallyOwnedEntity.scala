package cz.payola.domain.entities

trait OptionallyOwnedEntity extends cz.payola.common.entities.OptionallyOwnedEntity
{
    type UserType = User

    protected def checkInvariants() {
        require(owner != null, "Owner of the entity mustn't be null.")
    }
}
