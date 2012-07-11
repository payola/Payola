package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.squeryl.repositories._
import cz.payola.domain.entities.privileges._
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.plugins.DataSource
import scala.collection._
import cz.payola.domain.entities.Privilege

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.domain.entities.PrivilegableEntity
{
    self: PersistableEntity =>

    _privileges = null

    implicit val context: SquerylDataContextComponent

    override def privileges = {
        if (_privileges == null) {
            _privileges = context.privilegeRepository.getByGrantee(id).toBuffer
        }

        _privileges.toList
    }

    override def storePrivilege(privilege: PrivilegeType) {
        // Call domain method to preserve functionality
        super.storePrivilege(context.privilegeRepository.persist(privilege))
    }


    override def discardPrivilege(privilege: PrivilegeType) {
        // Call domain method to preserve functionality
        super.discardPrivilege(privilege)

        context.privilegeRepository.removeById(privilege.id)
    }
}
