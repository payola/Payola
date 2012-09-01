package cz.payola.data.squeryl.entities.privileges

import cz.payola.domain.entities.Privilege
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities._

/**
 * This object converts [[cz.payola.common.entities.Privilege]] to
 * [[cz.payola.data.squeryl.entities.PrivilegeDbRepresentation]] in order to be persisted in database.
 */
object PrivilegeDbRepresentation extends EntityConverter[PrivilegeDbRepresentation]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent) = {
        entity match {
            case p: PrivilegeDbRepresentation => Some(p)
            case privilege: Privilege[_] => {
                Some(new PrivilegeDbRepresentation(
                    privilege.id,
                    privilege.granter.id,
                    privilege.grantee.id,
                    privilege.grantee.className,
                    privilege.getClass.getName,
                    privilege.obj.id,
                    privilege.obj.className
                ))
            }
            case _ => None
        }
    }
}

/**
 * Represents [[cz.payola.common.entities.Privilege]] in order to be persisted in database.
 *
 * @param id -ID of the privilege
 * @param granterId ID of [[cz.payola.common.entities.User]] that granted the Privilege
 * @param granteeId ID of [[cz.payola.common.entities.PrivilegeableEntity]] that is being granted the Privilege
 * @param granteeClassName Stripped class name of this PrivilegeableEntity
 * @param privilegeClass Class of the Privilege
 * @param objectId ID of [[cz.payola.common.entities.Entity]] that is object of the Privilede
 * @param objectClassName Stripped class name of this Object
 */
class PrivilegeDbRepresentation(
    override val id: String,
    val granterId: String,
    val granteeId: String,
    val granteeClassName: String,
    val privilegeClass: String,
    val objectId: String,
    val objectClassName: String)(implicit val context: SquerylDataContextComponent)
    extends Entity
{
    override def classNameText = "privilege database representation"

    /**
     * Instantiates represented [[cz.payola.common.entities.Privilege]]
     * @return Returns instantiated privilege
     */
    def toPrivilege: cz.payola.common.entities.Privilege[_] = {
        context.privilegeRepository.getById(id).getOrElse(null)
    }
}
