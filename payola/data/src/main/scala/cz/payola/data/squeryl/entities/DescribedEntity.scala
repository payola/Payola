package cz.payola.data.squeryl.entities

import cz.payola.domain.Entity

trait DescribedEntity extends cz.payola.domain.entities.DescribedEntity
{
    self: Entity =>

    var _desc: String

    // Restore description value from DB
    description = _desc
}
