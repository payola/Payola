package cz.payola.domain.entities.generic

import cz.payola.common.entities.OwnedEntity
import cz.payola.domain.entities.User

trait ConcreteOwnedEntity extends ConcreteEntity with OwnedEntity
{
    type UserType = User

    protected val _ownerID: String = ""

    def isOwnedByUser(u: UserType): Boolean = _ownerID == u.id
}
