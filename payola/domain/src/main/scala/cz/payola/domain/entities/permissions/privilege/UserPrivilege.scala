package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.User
import cz.payola.domain.entities.permissions.action.Action

abstract class UserPrivilege[T <: Action[User]](o: User) extends Privilege[T, User](o)
{
}
