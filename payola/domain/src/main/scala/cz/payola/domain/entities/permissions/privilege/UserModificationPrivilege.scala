package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.User

/** This privilege grants the user to modify somehow the user @o.
  *
  * // TODO kinds of modifications.
  *
  * @param o A user which is the subject of this privilege.
  */
class UserModificationPrivilege(o: User) extends UserPrivilege(o)
{
}
