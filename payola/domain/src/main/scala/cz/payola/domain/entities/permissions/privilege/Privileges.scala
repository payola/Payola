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
abstract class Privilege[A <: Entity](val obj: A) extends Entity with common.entities.permissions.privilege.Privilege[A]

/**
  * Any privilege that extends this trait is considered public and hence safe to be transferred to the
  * client side - i.e. serialized. The User class will filter the privileges and return only those that
  * are public.
  */
trait PublicPrivilege

