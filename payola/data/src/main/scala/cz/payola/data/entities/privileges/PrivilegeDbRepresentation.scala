package cz.payola.data.entities.privileges

import cz.payola.data.entities.PersistableEntity
import cz.payola.common.entities

/**
  * This object converts [[cz.payola.common.entities.Privilege]] to [[cz.payola.data.entities.PrivilegeDbRepresentation]]
  * in order to be persisted in database.
  */
object PrivilegeDbRepresentation {

   def apply(privilege: entities.Privilege[_], granter: entities.User, grantee: entities.PrivilegableEntity) = {
        new PrivilegeDbRepresentation(
            privilege.id,
            granter.id,
            grantee.id,
            grantee.getClass.toString,
            privilege.getClass.toString,
            privilege.obj.asInstanceOf[entities.Entity].id,
            privilege.obj.getClass.toString
        )
   }
}

/**
  * Represents [[cz.payola.common.entities.Privilege]] in order to be persisted in database.
  *
  * @param id - id of a privilege
  * @param granterId - id of [[cz.payola.common.entities.User]] that granted the Privilege
  * @param granteeId - id of [[cz.payola.common.entities.PrivilegableEntity]] that is being granted the Privilege
  * @param granteeClass - class of this PrivilegableEntity
  * @param privilegeClass - class of the Privilege
  * @param objectId - id of [[cz.payola.common.entities.Entity]] that is object of the Privilede
  * @param objectClass - class of this Object
  */
class PrivilegeDbRepresentation(
        override val id: String,
        val granterId: String,
        val granteeId: String,
        val granteeClass: String,
        val privilegeClass: String,
        val objectId: String,
        val objectClass: String
    )
    extends PersistableEntity
{

}
