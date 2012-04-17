package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.User
import cz.payola.domain.entities.permissions.action.UserModificationAction

class UserModificationPrivilege(o: User) extends UserPrivilege[UserModificationAction](o)
{
}
