package cz.payola.model.permission.privilege

import cz.payola.model.User
import cz.payola.model.permission.action.Action

abstract class UserPrivilege[T <: Action[_]](o: User) extends Privilege[T, User](o)
