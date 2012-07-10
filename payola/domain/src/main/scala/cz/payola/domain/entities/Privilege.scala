package cz.payola.domain.entities

import cz.payola.domain._

abstract class Privilege[A <: Entity](val granter: User, val grantee: PrivilegableEntity, val obj: A, id: String)
    extends Entity(id) with cz.payola.common.entities.Privilege[A]
{
    type UserType = User

    type PrivilegableEntityType = PrivilegableEntity
}
