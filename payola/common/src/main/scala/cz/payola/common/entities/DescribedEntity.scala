package cz.payola.common.entities

/**
  * A described generic entity.
  */
trait DescribedEntity extends Entity
{
    protected var _description: String

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
