package cz.payola.model.generic

import cz.payola.model
import cz.payola.common.model.OwnedEntity

trait ConcreteOwnedEntity extends ConcreteEntity with OwnedEntity
{
    type UserType = model.User

    protected val _ownerID: String = ""

    def isOwnedByUser(u: UserType): Boolean = _ownerID == u.id

}
