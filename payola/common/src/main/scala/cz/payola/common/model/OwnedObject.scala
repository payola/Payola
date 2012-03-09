package cz.payola.common.model

trait OwnedObject {
    protected var _owner: User = null

    /** Returns the owner.
      *
      *  @return Owner.
      */
    def owner: User = _owner

    /** Sets the owner.
      *
      * @param u The owner.
      *
      * @throws IllegalArgumentException if the new user is null.
      */
    def owner_=(u: User) = {
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
    def setOwner(u: User) = owner_=(u);
}
