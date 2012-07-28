package cz.payola.web.shared.managers

import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities._
import cz.payola.model._
import s2js.compiler._

@remote class ShareableEntityManager[A <: ShareableEntity with OptionallyOwnedEntity with NamedEntity, B >: A]
    (val model: EntityModelComponent#ShareableEntityModel[A])
{
    @secured @async def getAccessible(user: Option[User] = null)
        (successCallback: Seq[B] => Unit)
        (errorCallback: Throwable => Unit) {

        successCallback(model.getAccessibleToUser(user))
    }
}
