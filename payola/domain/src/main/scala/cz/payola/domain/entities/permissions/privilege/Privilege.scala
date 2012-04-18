package cz.payola.domain.entities.permissions.privilege

import cz.payola.common
import cz.payola.domain.entities.Entity
import cz.payola.domain.entities.permissions.action.Action

abstract class Privilege[T <: Action[U], U <: Entity](val obj: U)
    extends common.entities.permissions.privilege.Privilege[T, U]
{
    def canPerformAction(action: T): Boolean = action.subject.id == obj.id
}
