package cz.payola.common.entities

import scala.collection.mutable

/**
  * A group of users.
  */
trait Group extends NamedEntity
{
    type UserType <: User

    protected var _owner: UserType

    protected val _members = new mutable.ArrayBuffer[UserType]()

    /** Owner of the group. */
    def owner = _owner

    /** Members of the group. */
    def members: Seq[UserType] = _members

    /**
      * Stores the specified member to the group.
      * @param user The member to store.
      */
    protected def storeMember(user: UserType) {
        _members += user
    }

    /**
      * Discards the specified member from the group. Complementary operation to store.
      * @param user The member to discard.
      */
    protected def discardMember(user: UserType) {
        _members -= user
    }
}
