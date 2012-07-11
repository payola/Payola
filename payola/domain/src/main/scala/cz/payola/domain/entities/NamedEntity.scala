package cz.payola.domain.entities

import cz.payola.domain.Entity

trait NamedEntity extends cz.payola.common.entities.NamedEntity
{
    self: Entity =>

    protected def checkInvariants() {
        require(name != null, "Name of the entity mustn't be null.")
    }
}
