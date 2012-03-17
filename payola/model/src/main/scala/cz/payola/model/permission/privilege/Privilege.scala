package cz.payola.model.permission.privilege

import cz.payola.model.generic.ConcreteEntity
import cz.payola.model.permission.action.Action

abstract class Privilege[T <: Action[_], U <: ConcreteEntity](var _object: U) {
    def canPerformAction(action: T): Boolean = action.subject == _object
}
