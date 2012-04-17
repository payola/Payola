package cz.payola.domain.entities.permissions.privilege

import cz.payola.common
import cz.payola.domain.entities.generic.ConcreteEntity

abstract class Privilege[U <: ConcreteEntity](val obj: U) extends common.entities.permissions.privilege.Privilege[U]
{

}
