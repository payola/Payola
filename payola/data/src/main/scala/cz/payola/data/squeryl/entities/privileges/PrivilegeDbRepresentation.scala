package cz.payola.data.squeryl.entities.privileges

import cz.payola.common._
import cz.payola.data.squeryl.SquerylDataContextComponent
import scala.Some
import cz.payola.domain.entities.Privilege
import cz.payola.data.squeryl.entities._

/**
  * This object converts [[cz.payola.common.entities.Privilege]] to [[cz.payola.data.squeryl.entities.PrivilegeDbRepresentation]]
  * in order to be persisted in database.
  */
object PrivilegeDbRepresentation extends EntityConverter[PrivilegeDbRepresentation] {

    def apply(privilege: entities.Privilege[_ <: Entity])
        (implicit context: SquerylDataContextComponent) = {
        // Plugins has to be handled slightly differently
        val granteeClass = context.repositoryRegistry.getClassName(privilege.grantee.getClass)
        val objectClass = context.repositoryRegistry.getClassName(
            privilege.obj match {
                case p: cz.payola.domain.entities.Plugin => classOf[cz.payola.domain.entities.Plugin]
                case o => o.getClass
            }
        )
        
        new PrivilegeDbRepresentation(
            privilege.id,
            privilege.granter.id,
            privilege.grantee.id,
            granteeClass,
            privilege.getClass.getName,
            privilege.obj.id,
            objectClass
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
  * @param granteeClassName - stripped class name of this PrivilegableEntity
  * @param privilegeClass - class of the Privilege
  * @param objectId - id of [[cz.payola.common.entities.Entity]] that is object of the Privilede
  * @param objectClassName - stripped class name of this Object
  */
class PrivilegeDbRepresentation(
        override val id: String,
        val granterId: String,
        val granteeId: String,
        val granteeClassName: String,
        val privilegeClass: String,
        val objectId: String,
        val objectClassName: String
    )
    (implicit val context: SquerylDataContextComponent)
    extends PersistableEntity
{

    /**
      * Instantiates represented [[cz.payola.common.entities.Privilege]]
      * @return Returns instantiated privilege
      */
    def toPrivilege: cz.payola.common.entities.Privilege[_] = {
        context.privilegeRepository.getById(id).getOrElse(null)
    }
}
