package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities.permissions.action.Action
import cz.payola.common.entities.Entity

trait Privilege[T <: Action[_], U <: Entity]
{
    protected val obj: U

    def canPerformAction(action: T): Boolean
}
