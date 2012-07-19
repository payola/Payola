package cz.payola.web.shared

import cz.payola.domain.entities._
import s2js.compiler._
import cz.payola.common.entities.privileges.ShareableEntityType
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities.privileges._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.domain.Entity

@secured
@remote object SharingData
{
    @async
    def setIsPublic(entityType: String, entityId: String, isPublic: Boolean = true, user: User = null)
        (successCallback: Boolean => Unit)
        (failCallback: Throwable => Unit) {
        val sharedEntities = getOwnedEntitiesByType(entityType, user)
        makeEntityPublic(sharedEntities, entityId, isPublic)
        successCallback(true)
    }

    @async
    def shareToGroup(entityType: String, entityId: String, groupIds: String,
        user: User = null)
        (successCallback: Boolean => Unit)
        (failCallback: Throwable => Unit) {
        val groupIdsList = groupIds.split(',')
        val groups = user.ownedGroups.filter { g => !groupIdsList.contains(g.id)}
        shareEntityToPrivilegableEntity(entityType, entityId, user, groups)
        successCallback(true)
    }

    @async
    def shareToUser(entityType: String, entityId: String, userIds: String,
        user: User = null)
        (successCallback: Boolean => Unit)
        (failCallback: Throwable => Unit) {

        val userIdsList = userIds.split(',')
        val shareTo = Payola.model.userModel.getByIds(userIdsList)
        shareEntityToPrivilegableEntity(entityType, entityId, user, shareTo)
        successCallback(true)
    }

    private def shareEntityToPrivilegableEntity(entityType: String, entityId: String, granter: User,
        shareTo: Seq[Entity with PrivilegableEntity]) {
        val ownedEntities = getOwnedEntitiesByType(entityType, granter)
        ownedEntities.find(_.id == entityId).map { sharedEntity =>
            shareTo.foreach { grantee =>
                destroyPrivilege(sharedEntity, granter, grantee)

                val privilege = createPrivilegeByType(sharedEntity, granter, grantee)
                grantee.grantPrivilege(privilege)
            }
        }.getOrElse {
            throw new Exception("Shared entity not found.")
        }
    }

    private def createPrivilegeByType(sharedEntity: ShareableEntity, granter: User,
        grantee: Entity with PrivilegableEntity): Privilege[_ <: Entity] = {

        sharedEntity match {
            case e: Analysis => new AccessAnalysisPrivilege(granter, grantee, e)
            case e: Plugin => new UsePluginPrivilege(granter, grantee, e)
            case e: DataSource => new AccessDataSourcePrivilege(granter, grantee, e)
            case e: OntologyCustomization => new UseOntologyCustomizationPrivilege(granter, grantee, e)
            case _ => throw new Exception("Unknown entity type.")
        }

    }

    private def destroyPrivilege(sharedEntity: Entity with ShareableEntity, granter: User, grantee: PrivilegableEntity) {
        grantee.privileges.find(p => (p.granter == granter) && (p.obj == sharedEntity)).map { p =>
            grantee.removePrivilege(p)
        }
    }

    private def getOwnedEntitiesByType(entityType: String, user: User): Seq[Entity with ShareableEntity] = {
        entityType match {
            case ShareableEntityType.analysis => user.ownedAnalyses
            case ShareableEntityType.plugin => user.ownedPlugins
            case ShareableEntityType.dataSource => user.ownedDataSources
            case ShareableEntityType.customization => user.ownedOntologyCustomizations
            case _ => throw new Exception("Unknown entity type.")
        }
    }

    private def makeEntityPublic(ownedEntities: Seq[Entity with ShareableEntity], entityId: String, isPublic: Boolean = true) {
        ownedEntities.find(e => e.id == entityId).map { e =>
            e.isPublic_=(isPublic)
            Payola.model.persistEntity(e)
        }
    }
}
