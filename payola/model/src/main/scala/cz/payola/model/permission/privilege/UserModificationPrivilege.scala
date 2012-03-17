package cz.payola.model.permission.privilege

import cz.payola.model.permission.action.UserModificationAction
import cz.payola.model.User

class UserModificationPrivilege(o: User) extends UserPrivilege[UserModificationAction](o) {
}
