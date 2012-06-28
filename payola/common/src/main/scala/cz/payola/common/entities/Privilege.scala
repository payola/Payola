package cz.payola.common.entities

/**
  * A privilege that a privilegable entity may be granted. Grants the entity rights around the specified object.
  * @tparam A Object of the privilege.
  */
trait Privilege[+A <: Entity] extends Entity
{
    val obj: A
}
