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
            stripClassName(grantee.getClass.toString),
            stripClassName(privilege.getClass.toString),
            privilege.obj.asInstanceOf[entities.Entity].id,
            stripClassName(privilege.obj.getClass.toString)
        )
   }

    /**
      * Modifies full-class name to format stored to database.
      *
      * @param className - full-class name of an object
      * @return Returns properly-formatted class name
      */
    def stripClassName(className: String): String = {
        var n = className.replace("class ", "")
        val pos = n.lastIndexOf(".")

        // Return class name - User, Group, ... (+1 means skip '.')
        n.substring(pos + 1)
    }
}

/**
  * Represents [[cz.payola.common.entities.Privilege]] in order to be persisted in database.
  *
  * @param id - id of a privilege
  * @param granterId - id of [[cz.payola.common.entities.User]] that granted the Privilege
  * @param granteeId - id of [[cz.payola.common.entities.PrivilegableEntity]] that is being granted the Privilege
  * @param granteeClass - stripped class of this PrivilegableEntity
  * @param privilegeClass - stripped class of the Privilege
  * @param objectId - id of [[cz.payola.common.entities.Entity]] that is object of the Privilede
  * @param objectClass - stripped class of this Object
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
