package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common._
import cz.payola.domain.entities._
import cz.payola.common.entities.ShareableEntity

@remote @secured object SharingData
{
    private type GranteeType = cz.payola.common.Entity with cz.payola.common.entities.PrivilegableEntity

    @async def getEntityGrantees(entityClassName: String, entityId: String, granteeClassName: String, user: User = null)
        (successCallback: Seq[GranteeType] => Unit)
        (failCallback: Throwable => Unit) {

        val entity = getShareableEntity(user, entityClassName, entityId)
        val privilegeClass = Payola.model.privilegeModel.getSharingPrivilegeClass(entity)
        val privileges = Payola.model.privilegeModel.getAllByObjectIdAndPrivilegeClass(entity.id, privilegeClass)
        successCallback(privileges.map(_.grantee))
    }

    @async def getPotentialGrantees(granteeClassName: String, user: User = null)
        (successCallback: Seq[GranteeType] => Unit)
        (failCallback: Throwable => Unit) {

        successCallback(Payola.model.privilegeModel.getPotentialGrantees(granteeClassName, user))
    }

    @async def searchPotentialGrantees(granteeClassName: String, searchTerm: String, user: User = null)
        (successCallback: Seq[GranteeType] => Unit)
        (failCallback: Throwable => Unit) {

        getPotentialGrantees(granteeClassName, user) { grantees =>
            successCallback(grantees.filter(_.name.toLowerCase.contains(searchTerm.toLowerCase.trim)))
        }(failCallback(_))
    }

    @async def setEntityPublicity(entityClassName: String, entityId: String, isPublic: Boolean, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        val entity = getShareableEntity(user, entityClassName, entityId)
        entity.isPublic = isPublic
        Payola.model.persistEntity(entity)
        successCallback()
    }

    @async def shareEntity(entityClassName: String, entityId: String, granteeClassName: String, granteeIds: String,
        user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        val sharedEntity = getShareableEntity(user, entityClassName, entityId)
        Payola.model.privilegeModel.shareEntity(sharedEntity, granteeClassName, granteeIds.split(","), user)
        successCallback()
    }

    private def getShareableEntity(owner: User, entityClassName: String, entityId: String): ShareableEntity = {
        owner.getOwnedShareableEntity(entityClassName, entityId).getOrElse {
            throw new PayolaException("The user doesn't own the specified entity.")
        }
    }
}
