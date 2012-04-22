package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.User

/** This class narrows down the privilege subject to some other (or same) user.
  *
  * @param o A user which is the subject of this privilege.
  */
abstract class UserPrivilege(o: User) extends Privilege[User](o) {

}
