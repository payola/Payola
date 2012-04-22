package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Group

/**This class narrows down the privilege subject to some group.
  *
  * @param g A group which is the subject of this privilege.
  */
abstract class GroupPrivilege(g: Group) extends Privilege[Group](g)
{
}
