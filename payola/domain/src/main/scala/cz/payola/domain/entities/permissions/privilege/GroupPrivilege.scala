package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Group
import cz.payola.domain.permission.action.Action

abstract class GroupPrivilege[T <: Action[_]](g: Group) extends Privilege[T, Group](g)
{
}
