package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.generic.ConcreteEntity
import cz.payola.domain.permission.action.Action

abstract class Privilege[T <: Action[_], U <: ConcreteEntity](var _object: U)
{
    def canPerformAction(action: T): Boolean = action.subject == _object
}
