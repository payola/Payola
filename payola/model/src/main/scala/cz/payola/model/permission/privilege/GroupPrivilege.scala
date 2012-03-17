package cz.payola.model.permission.privilege

import cz.payola.model.Group
import cz.payola.model.permission.action.Action

abstract class GroupPrivilege[T <: Action[_]](g: Group) extends Privilege[T, Group](g) {

}
