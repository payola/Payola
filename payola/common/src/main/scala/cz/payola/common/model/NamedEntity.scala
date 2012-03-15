package cz.payola.common.model

trait NamedEntity extends Entity
{
    protected var _name: String

    def name = _name

    def name_=(value: String) {
        _name = value
    }
}
