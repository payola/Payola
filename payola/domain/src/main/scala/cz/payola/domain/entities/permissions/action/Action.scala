package cz.payola.domain.permission.action

import cz.payola.common
import cz.payola.domain.entities.generic.ConcreteEntity

abstract class Action[EntityType](val subject: EntityType) extends common.entities.permissions.action.Action[EntityType]
{
    type EntityType = ConcreteEntity
}
