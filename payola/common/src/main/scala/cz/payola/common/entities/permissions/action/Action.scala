package cz.payola.common.entities.permissions.action

import cz.payola.common.entities.Entity

trait Action[T <: Entity]
{
    val subject: T
}
