package cz.payola.domain.entities.permissions.action

import cz.payola.common
import cz.payola.domain.entities.generic.ConcreteEntity

abstract class Action[T <: ConcreteEntity](val subject: T) extends common.entities.permissions.action.Action[T]
{

}
