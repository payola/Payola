package cz.payola.domain.entities.permissions.action

import cz.payola.common
import cz.payola.domain.entities.Entity

abstract class Action[T <: Entity](val subject: T) extends common.entities.permissions.action.Action[T]
{
}
