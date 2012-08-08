package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.Entity

/**
 * Provides persistence to [[cz.payola.common.entities.DescribedEntity]] entities.
 */
trait DescribedEntity extends Entity with cz.payola.common.entities.DescribedEntity
{
    var _desc: String

    // Restore description value from DB
    description = _desc

    override def description_=(value: String) {
        _desc = value
        super.description = value
    }
}
