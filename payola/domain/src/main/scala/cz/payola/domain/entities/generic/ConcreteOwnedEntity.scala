package cz.payola.domain.entities.generic

import cz.payola.common.entities.OwnedEntity
import cz.payola.domain.entities.User

trait ConcreteOwnedEntity extends OwnedEntity
{
    type UserType = User

    protected val _owner: UserType

    def isOwnedByUser(u: UserType): Boolean = _owner.id == u.id
}
