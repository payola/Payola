package cz.payola.model.permission.action

import cz.payola.model.generic.ConcreteEntity

abstract class Action[T <: ConcreteEntity](val subject: T) {

}
