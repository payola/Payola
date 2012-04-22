package cz.payola.domain.entities.generic

import cz.payola.common.entities.Entity

/** Defines a concrete entity which is the root class of all entities at the domain level.
  *
  * @param id Entity ID.
  */
class ConcreteEntity(override val id: String) extends Entity

