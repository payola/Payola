package cz.payola.common.entities

import scala.collection._
import cz.payola.common.Entity

/**
  * A user-defined group of users. The user may share shareable entities not only to other users, but also to groups
  * of users.
  */
trait Group extends Entity with NamedEntity with PrivilegeableEntity
{
    /** Type of the users that can be members of a group. */
    type UserType <: User

    protected var _owner: UserType

    protected var _members = new mutable.ArrayBuffer[UserType]()

    override def classNameText = "user group"

    /** Owner of the group. */
    def owner = _owner

    /** Members of the group. */
    def members: immutable.Seq[UserType] = _members.toList

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
