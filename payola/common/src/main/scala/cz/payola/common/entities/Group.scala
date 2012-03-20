package cz.payola.common.entities

import scala.collection
import scala.collection.mutable

trait Group extends NamedEntity with OwnedEntity
{
    protected val _members: mutable.Seq[UserType]

    def members: collection.Seq[UserType] = _members
}
