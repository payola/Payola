package cz.payola.domain.entities.permissions.privilege

import cz.payola.common
import cz.payola.domain.entities.Entity

/** An abstract class that grants the user some privilege over @obj.
  *
  * The particular privilege is defined in within the subclasses.
  *
  * @param obj Object, over which is the user granted a privilege.
  * @tparam A A subclass of the ConcreteEntity trait.
  */
abstract class Privilege[A <: Entity](val obj: A) extends common.entities.permissions.privilege.Privilege[A] {

}
