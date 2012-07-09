package cz.payola.data.squeryl.entities.privileges

import cz.payola.common._
import cz.payola.data.squeryl.entities._
import cz.payola.data.DataException
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.entities.Privilege

/**
  * This object converts [[cz.payola.common.entities.Privilege]] to [[cz.payola.data.squeryl.entities.PrivilegeDbRepresentation]]
  * in order to be persisted in database.
  */
object PrivilegeDbRepresentation extends EntityConverter[PrivilegeDbRepresentation] {

    def apply(privilege: entities.Privilege[_ <: Entity])
        (implicit context: SquerylDataContextComponent) = {
        new PrivilegeDbRepresentation(
            privilege.id,
            privilege.granter.id,
            privilege.grantee.id,
            context.repositoryRegistry.getClassName(privilege.grantee.getClass),
            privilege.getClass.getName,
            privilege.obj.id,
            context.repositoryRegistry.getClassName(privilege.obj.getClass)
        )
    }

    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent) = {
        entity match {
            case p: PrivilegeDbRepresentation => Some(p)
            case p: Privilege[_] => Some(PrivilegeDbRepresentation(p))
            case _ => None
        }
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
    (implicit val context: SquerylDataContextComponent)
    extends PersistableEntity {

    /**
      * Instantiates represented [[cz.payola.common.entities.Privilege]]
      * @return Returns instantiated privilege
      */
    def toPrivilege: cz.payola.common.entities.Privilege[_] = {
        context.privilegeRepository.getById(id).get
    }
}
