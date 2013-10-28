package cz.payola.model.components

import cz.payola.common.Entity
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities._
import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.privileges._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.settings._
import cz.payola.data.DataContextComponent
import cz.payola.model._

trait PrivilegeModelComponent extends EntityModelComponent
{
    self: DataContextComponent with UserModelComponent =>

    lazy val privilegeModel = new
    {
        def getEntityGrantees(objectEntity: ShareableEntity, privilegeClass: Class[_],
            granteeClassName: String): Seq[PrivilegeableEntity] = {

            val privileges = privilegeRepository.getAllByObjectIdAndPrivilegeClass(objectEntity.id, privilegeClass)
            val grantees = privileges.map(_.grantee)
            val entities = repositoryRegistry(granteeClassName).getByIds(grantees.map(_.id))
            grantees.filter(entities.contains(_))
        }

        def getPotentialGrantees(granteeClassName: String, user: User): Seq[PrivilegeableEntity] = {
            Map(
                Entity.getClassName(classOf[User]) -> (() => userModel.getAll()),
                Entity.getClassName(classOf[Group]) -> (() => user.ownedGroups)
            ).get(granteeClassName).map(_()).getOrElse {
                throw new ModelException("Invalid grantee class " + granteeClassName + ".")
            }
        }

        def shareEntity(entity: ShareableEntity, granteeClassName: String, granteeIds: Seq[String], user: User) {
            val grantees = getPotentialGrantees(granteeClassName, user).filter(g => granteeIds.contains(g.id))
            val privilegeClass = getSharingPrivilegeClass(entity)
            val privileges = privilegeRepository.getAllByObjectIdAndPrivilegeClass(entity.id, privilegeClass)

            // Remove the removed privileges.
            privileges.filter(p => !grantees.contains(p.grantee) && p.grantee.className == granteeClassName).foreach {
                p => p.grantee.removePrivilege(p)
            }

            // Grant the new ones.
            grantees.filter(g => !privileges.exists(_.grantee == g)).foreach { g =>
                g.grantPrivilege(createSharingPrivilege(user, g, entity))
            }
        }

        def getSharingPrivilegeClass(objectEntityClass: Class[_]): Class[_] = {
            Map(
                Entity.getClassName(classOf[Analysis]) -> classOf[AccessAnalysisPrivilege],
                Entity.getClassName(classOf[Plugin]) -> classOf[UsePluginPrivilege],
                Entity.getClassName(classOf[DataSource]) -> classOf[AccessDataSourcePrivilege],
                Entity.getClassName(classOf[Customization]) -> classOf[UseCustomizationPrivilege]
            ).getOrElse(Entity.getClassName(objectEntityClass), throw new ModelException(
                "The entity of class %s doesn't have a sharing privilege.".format(objectEntityClass.getName))
            )
        }

        def getSharingPrivilegeClass(objectEntity: ShareableEntity): Class[_] = {
            val objectClass = objectEntity match {
                case _: Plugin => classOf[Plugin]
                case _ => objectEntity.getClass
            }
            getSharingPrivilegeClass(objectClass)
        }

        private def createSharingPrivilege(granter: User, grantee: PrivilegeableEntity, objectEntity: ShareableEntity):
            Privilege[_ <: Entity with ShareableEntity] = {

            objectEntity match {
                case a: Analysis => new AccessAnalysisPrivilege(granter, grantee, a)
                case p: Plugin => new UsePluginPrivilege(granter, grantee, p)
                case d: DataSource => new AccessDataSourcePrivilege(granter, grantee, d)
                case c: Customization => new UseCustomizationPrivilege(granter, grantee, c)
                case _ => {
                    throw new ModelException("The entity of class %s doesn't have a sharing privilege.".format(
                        objectEntity.getClass.getName))
                }
            }
        }
    }
}
