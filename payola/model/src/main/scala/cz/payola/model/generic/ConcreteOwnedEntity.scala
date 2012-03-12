package cz.payola.model.generic

import cz.payola.model
import cz.payola.common.model.OwnedEntity

trait ConcreteOwnedEntity extends ConcreteEntity with OwnedEntity
{
    type UserType = model.User

    protected var _owner: UserType = null

    /** Returns the owner.
      *
      *  @return Owner.
      */
    def owner = _owner

    def isOwnedByUser(u: UserType): Boolean = owner == u

    /** Sets the owner.
      *
      * @param u The owner.
      *
      * @throws IllegalArgumentException if the new user is null.
      */
    def owner_=(u: UserType) = {
        // Owner mustn't be null
        require(u != null)

        _owner = u
    }

    /** Convenience method that just calls owner_=.
      *
      * @param u The new owner.
      *
      * @throws IllegalArgumentException if the user is null.
      */
    def setOwner(u: UserType) = owner_=(u);
}
