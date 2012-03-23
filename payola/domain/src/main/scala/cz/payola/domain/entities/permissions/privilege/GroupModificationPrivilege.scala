package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Group
import cz.payola.domain.entities.permissions.action.GroupModificationAction

class GroupModificationPrivilege(g: Group) extends GroupPrivilege(g)
{
    type ActionType = GroupModificationAction
}
