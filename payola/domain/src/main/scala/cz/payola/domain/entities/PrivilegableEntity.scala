package cz.payola.domain.entities

import scala.collection._
import cz.payola.common.entities.privileges._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.domain.entities.privileges.PublicPrivilege

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.common.entities.PrivilegableEntity
{
    /** Type of the privileges. */
    type PrivilegeType = Privilege[_]

    /**
      * Grants given permission to this entity
      * @param granter - User that grants privilege
      * @param privilege - Privilege that is granted to this entity
      */
    def grantPrivilege(granter: cz.payola.common.entities.User, privilege: PrivilegeType) {
        storePrivilege(granter, privilege)
    }

    /**
      * Denies granted privilege to this entity
      * @param privilege - granted privilege that will be denied
      */
    def denyPrivilege(privilege: PrivilegeType) {
        discardPrivilege(privilege)
    }
}
