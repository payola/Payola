package cz.payola.common.entities

import cz.payola.common.Entity

/**
  * An entity that may or may not be owned.
  */
trait OptionallyOwnedEntity extends Entity
{
    /** Type of the user who may own the entity. */
    type UserType <: User

    protected var _owner: Option[UserType]

    /** Owner of the entity. */
    def owner = _owner
}
