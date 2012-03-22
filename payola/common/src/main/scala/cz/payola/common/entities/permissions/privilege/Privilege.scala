package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities.permissions.action.Action
import cz.payola.common.entities.Entity

trait Privilege[ActionType, EntityType] {

    type ActionType <: Action[_]
    type EntityType <: Entity

    //protected val _object: EntityType

    def canPerformAction(action: ActionType): Boolean

}
