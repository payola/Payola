package cz.payola.domain.entities

import cz.payola.common.Entity

trait PrivilegeableEntity extends NamedEntity with cz.payola.common.entities.PrivilegeableEntity
{
    type PrivilegeType = Privilege[_ <: Entity]

    /**
      * Adds a new privilege to the entity.
      * @param privilege The privilege to add.
      * @throws IllegalArgumentException if the privilege can't be added to the entity.
      */
    def grantPrivilege(privilege: PrivilegeType) {
        addRelatedEntity(privilege, privileges, storePrivilege)
    }

    /**
      * Removes the specified privilege from the entity.
      * @param privilege The privilege to be removed.
      * @return The removed privilege.
      */
    def removePrivilege(privilege: PrivilegeType): Option[PrivilegeType] = {
        removeRelatedEntity(privilege, privileges, discardPrivilege)
    }
}
