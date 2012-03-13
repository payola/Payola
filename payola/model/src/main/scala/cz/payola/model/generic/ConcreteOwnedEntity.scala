package cz.payola.model.generic

import cz.payola.model
import cz.payola.common.model.OwnedEntity
import cz.payola.scala2json.annotations._

trait ConcreteOwnedEntity extends ConcreteEntity with OwnedEntity
{
    type UserType = model.User

    @JSONFieldName(name = "owner") protected var _ownerID: String = ""
    @JSONTransient protected var _owner: UserType = null

    /** Returns the owner.
      *
      *  @return Owner.
      */
    def owner = _owner

    def isOwnedByUser(u: UserType): Boolean = _ownerID == u.id

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
        _ownerID = u.id
    }

    /** Convenience method that just calls owner_=.
      *
      * @param u The new owner.
      *
      * @throws IllegalArgumentException if the user is null.
      */
    def setOwner(u: UserType) = owner_=(u)
}
