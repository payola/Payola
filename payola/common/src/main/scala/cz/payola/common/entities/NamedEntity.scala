package cz.payola.common.entities

/**
  * A named generic entity.
  */
trait NamedEntity extends Entity
{
    protected var _name: String

    /** Name of the entity */
    def name = _name

    /**
      * Sets name of the entity.
      * @param value The value of new name.
      */
    def name_=(value: String) {
        require(value != null, "Name mustn't be null!")
        require(value != "", "Name mustn't be empty!")
        require(value.trim != "", "Name mustn't be just whitespace!")

        _name = value
    }
}
