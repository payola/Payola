package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities.Entity

trait Privilege[U <: Entity] extends Entity
{
    protected val obj: U
}
