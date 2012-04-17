package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Group

abstract class GroupPrivilege(g: Group) extends Privilege[Group](g)
{
}
