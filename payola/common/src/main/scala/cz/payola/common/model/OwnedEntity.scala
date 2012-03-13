package cz.payola.common.model

trait OwnedEntity extends Entity
{
    /** Type of the user who owns the entity. */
    type UserType <: User

    def owner: UserType
}
