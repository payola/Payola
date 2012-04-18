package cz.payola.common.entities

import scala.collection
import scala.collection.mutable

trait Group extends NamedEntity
{
    type UserType <: User

    protected val _owner: UserType

    protected val _members: mutable.Seq[UserType]

    def owner = _owner

    def members: collection.Seq[UserType] = _members
}
