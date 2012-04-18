package cz.payola.common.entities

trait ShareableEntity extends Entity
{
    protected var _isPublic: Boolean

    def isPublic = _isPublic

    def isPublic_=(value: Boolean) {
        _isPublic = value
    }
}
