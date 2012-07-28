package cz.payola.common.entities

import cz.payola.common.Entity

/**
  * A described generic entity.
  */
trait DescribedEntity extends Entity
{
    self: Entity =>

    protected var _description: String = ""

    /** Description of the entity */
    def description = _description

    /**
      * Sets description of the entity.
      * @param value The value of new description.
      */
    def description_=(value: String) {
        _description = value
    }
}
