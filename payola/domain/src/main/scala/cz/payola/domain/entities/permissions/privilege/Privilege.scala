package cz.payola.domain.entities.permissions.privilege

import cz.payola.common
import cz.payola.domain.entities.generic.ConcreteEntity

/** An abstract class that grants the user some privilege over @obj.
  *
  * The particular privilege is defined in within the subclasses.
  *
  * @param obj Object, over which is the user granted a privilege.
  * @tparam U A subclass of the ConcreteEntity trait.
  */
abstract class Privilege[U <: ConcreteEntity](val obj: U) extends common.entities.permissions.privilege.Privilege[U]
{

}
