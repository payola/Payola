package cz.payola.model.permission.action

import cz.payola.model.User

object UserModificationActionType extends Enumeration {
    type UserModificationActionType = Value
    val ChangeName, ChangePassword, ChangeEmail = Value
}

class UserModificationAction(u: User, val actionType: UserModificationActionType.Value, val newValue: Any) extends Action[User](u) {
}
