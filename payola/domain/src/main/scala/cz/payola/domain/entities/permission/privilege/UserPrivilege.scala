package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.User
import cz.payola.domain.permission.action.Action

abstract class UserPrivilege[T <: Action[_]](o: User) extends Privilege[T, User](o)
