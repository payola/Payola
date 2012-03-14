package cz.payola.common.model

trait OwnedEntity extends Entity
{
    /** Type of the user who owns the entity. */
    type UserType <: User

    protected val _owner: UserType
    
    def owner = _owner
}
