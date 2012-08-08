package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.Entity

/**
 * Provides persistence to [[cz.payola.common.entities.ShareableEntity]] entities.
 */
trait ShareableEntity extends Entity with cz.payola.common.entities.ShareableEntity
{
    // Set isPublic value into field that is persisted on DB
    var _isPub: Boolean

    // Restore publicity value from DB
    isPublic = _isPub

    override def isPublic_=(value: Boolean) {
        _isPub = value
        super.isPublic = value
    }
}
