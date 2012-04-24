package cz.payola.common.entities

trait NamedEntity extends Entity
{
    protected var _name: String

    def name = _name

    def name_=(value: String) {
        require(value != null, "Name mustn't be null!")
        require(value != "", "Name mustn't be empty!")
        require(value.trim != "", "Name mustn't be just whitespace!")

        _name = value
    }
}
