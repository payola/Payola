package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Group
import cz.payola.domain.entities.permissions.action.Action

abstract class GroupPrivilege[T <: Action[Group]](g: Group) extends Privilege[T, Group](g)
{
}
