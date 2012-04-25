package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities.Group
import cz.payola.common.entities.permissions.action.Action

trait GroupPrivilege[U <: Action[_]] extends Privilege[U, Group]
{
}
