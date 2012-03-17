package cz.payola.model.permission.privilege

import cz.payola.model.Group
import cz.payola.model.permission.action.GroupModificationAction

class GroupModificationPrivilege(g: Group) extends GroupPrivilege[GroupModificationAction](g) {
}
