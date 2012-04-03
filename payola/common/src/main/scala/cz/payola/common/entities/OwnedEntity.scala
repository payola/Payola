package cz.payola.common.entities

trait OwnedEntity extends Entity
{
    /** Type of the user who owns the entity. */
    type UserType <: User

    protected val _owner: UserType
    
    def owner = _owner
}
