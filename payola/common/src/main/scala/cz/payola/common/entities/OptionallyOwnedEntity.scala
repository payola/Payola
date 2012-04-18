package cz.payola.common.entities

trait OptionallyOwnedEntity extends Entity
{
    /** Type of the user who may own the entity. */
    type UserType <: User

    protected val _owner: Option[UserType]

    def owner = _owner
}
