package cz.payola.domain.entities

import cz.payola.domain._

abstract class Privilege[A <: Entity](
    val granter: User,
    val grantee: Entity with PrivilegableEntity,
    val obj: A,
    override val id: String)
    extends Entity with cz.payola.common.entities.Privilege[A]
{
    type UserType = User

    type PrivilegableEntityType = Entity with PrivilegableEntity
}
