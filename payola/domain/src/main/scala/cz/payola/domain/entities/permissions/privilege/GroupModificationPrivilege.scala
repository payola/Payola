package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Group

/** Allows the user to modify the group.
  *
  * // TODO modification types
  *
  * @param g A group which is the subject of this privilege.
  */
class GroupModificationPrivilege(g: Group) extends GroupPrivilege(g)
{

}
