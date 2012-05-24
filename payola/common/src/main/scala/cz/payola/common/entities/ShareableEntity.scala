package cz.payola.common.entities

/**
  * An entity that may be shared to single or groups of users. It may also be public which means that everyone may
  * access the entity.
  */
trait ShareableEntity extends Entity
{
    protected var _isPublic: Boolean = false

    /** Whether the entity is public. */
    def isPublic = _isPublic

    /**
      * Sets whether the entity is public.
      * @param value The new value of whether the entity is public.
      */
    def isPublic_=(value: Boolean) {
        _isPublic = value
    }
}
