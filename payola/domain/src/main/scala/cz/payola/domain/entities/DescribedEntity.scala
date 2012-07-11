package cz.payola.domain.entities

import cz.payola.domain.Entity

trait DescribedEntity extends cz.payola.common.entities.DescribedEntity
{
    self: Entity =>
}
