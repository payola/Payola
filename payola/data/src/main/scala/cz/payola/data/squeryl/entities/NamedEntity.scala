package cz.payola.data.squeryl.entities

import cz.payola.domain.Entity

trait NamedEntity extends cz.payola.domain.entities.NamedEntity
{
    self: Entity =>
}
