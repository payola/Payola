package cz.payola.domain.entities

import cz.payola.domain.Entity

trait OptionallyOwnedEntity extends Entity with cz.payola.common.entities.OptionallyOwnedEntity
{
    type UserType = User

    protected override def checkInvariants() {
        validate(owner != null, "owner", "Owner of the %s mustn't be null.".format(classNameText))
    }

    /**
     * @param value New owner of entity
     */
    def owner_=(value: Option[User]) {
        _owner = value
    }
}
