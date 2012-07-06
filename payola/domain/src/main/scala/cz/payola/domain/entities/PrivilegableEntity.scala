package cz.payola.domain.entities

import cz.payola.domain.Entity

trait PrivilegableEntity extends cz.payola.common.entities.PrivilegableEntity
{ self: Entity =>

    type PrivilegeType = Privilege[_]

    /**
      * Adds a new privilege to the entity.
      * @param privilege The privilege to add.
      * @param granter The user who is granting the privilege.
      * @throws IllegalArgumentException if the privilege can't be added to the entity.
      */
    def addPrivilege(privilege: PrivilegeType, granter: User) {
        addRelatedEntity(privilege, privileges, storePrivilege)
    }

    /**
      * Removes the specified privilege from the entity.
      * @param privilege The privilege to be removed.
      * @param granter The user who granted the privilege.
      * @return The removed privilege.
      */
    def removePrivilege(privilege: PrivilegeType, granter: User): Option[PrivilegeType] = {
        removeRelatedEntity(privilege, privileges, discardPrivilege)
    }
}
