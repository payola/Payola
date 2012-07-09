package cz.payola.common.entities

import cz.payola.common.Entity

/**
  * A privilege that a privilegable entity may be granted. Grants the entity rights around the specified object.
  * @tparam A Object of the privilege.
  */
trait Privilege[+A <: Entity] extends Entity
{
    /** Type of the users who may be granters of the privilege. */
    type UserType <: User

    /** Type of the entities that may be grated the privilege. */
    type PrivilegableEntityType <: PrivilegableEntity

    /** The user who granted the privilege. */
    val granter: UserType

    /** The enity that was granted the privilege. */
    val grantee: PrivilegableEntityType

    /** The object of privilege. */
    val obj: A
}
