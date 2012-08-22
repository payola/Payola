package cz.payola.domain.entities

import cz.payola.domain._

abstract class Privilege[A <: Entity](
    val granter: User,
    val grantee: Entity with PrivilegeableEntity,
    val obj: A,
    override val id: String)
    extends Entity with cz.payola.common.entities.Privilege[A]
{
    type UserType = User

    type PrivilegeableEntityType = Entity with PrivilegeableEntity
}
