package cz.payola.domain.permission.action

import cz.payola.domain.entities.generic.ConcreteEntity

abstract class Action[T <: ConcreteEntity](val subject: T)
{
}
