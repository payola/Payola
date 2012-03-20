package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Group
import cz.payola.domain.permission.action.GroupModificationAction

class GroupModificationPrivilege(g: Group) extends GroupPrivilege[GroupModificationAction](g)
{
}
