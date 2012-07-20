package cz.payola.common.entities

import cz.payola.common.Entity

/**
  * A named generic entity.
  */
trait NamedEntity extends Entity
{
    protected var _name: String

    /** Name of the entity. */
    def name = _name

    /**
      * Sets name of the entity.
      * @param value The value of new name.
      */
    def name_=(value: String) {
       _name = value
    }
}
