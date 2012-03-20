package cz.payola.domain.permission.action

import cz.payola.domain.entities.Group

object GroupModificationActionType extends Enumeration
{
    type GroupModificationActionType = Value

    val GroupName, AddUser, RemoveUser = Value
}

class GroupModificationAction(g: Group, val actionType: GroupModificationActionType.Value,
    val newValue: Any) extends Action[Group](g)
{
}
