package cz.payola.data.squeryl.entities

import cz.payola.domain.Entity

trait ShareableEntity extends Entity with cz.payola.domain.entities.ShareableEntity
{
    self: Entity =>
}
