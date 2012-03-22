package cz.payola.domain.permission.privilege

import cz.payola.common
import common.entities.Entity
import cz.payola.domain.entities.generic.ConcreteEntity
import cz.payola.domain.permission.action.Action

abstract class Privilege[ActionType, EntityType](val _object: EntityType) extends common.entities.permissions.privilege.Privilege[ActionType, EntityType]
{
    type ActionType = Action[_]
    type EntityType = ConcreteEntity

    def canPerformAction(action: ActionType): Boolean = action.subject == _object
}
