package cz.payola.common.entities

import scala.collection.mutable

/**
  * A group of users.
  */
trait Group extends NamedEntity
{
    type UserType <: User

    protected val _owner: UserType

    protected val _members: mutable.Seq[UserType]

    /** Owner of the group. */
    def owner = _owner

    /** Members of the group. */
    def members: Seq[UserType] = _members
}
