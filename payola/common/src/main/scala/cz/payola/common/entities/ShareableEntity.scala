package cz.payola.common.entities

import cz.payola.common.Entity

/**
  * An entity that may be shared to users or groups of users. It may also be public which means that everyone may
  * access the entity. If the entity is not public, then it's visible only to those who have been shared the entity.
  */
trait ShareableEntity extends Entity with NamedEntity
{
    protected var _isPublic: Boolean = false

    protected var _isVisibleInListings: Boolean = true

    /** Whether the entity is public. */
    def isPublic = _isPublic

    def isVisibleInListings = _isVisibleInListings

    /**
      * Sets whether the entity is public.
      * @param value The new value of whether the entity is public.
      */
    def isPublic_=(value: Boolean) {
        _isPublic = value
    }

    def isVisibleInListings_=(value: Boolean) {
        _isVisibleInListings = value
    }
}
