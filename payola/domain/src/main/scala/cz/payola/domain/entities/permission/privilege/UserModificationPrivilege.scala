package cz.payola.domain.permission.privilege

import cz.payola.domain.permission.action.UserModificationAction
import cz.payola.domain.entities.User

class UserModificationPrivilege(o: User) extends UserPrivilege[UserModificationAction](o)
{
}
