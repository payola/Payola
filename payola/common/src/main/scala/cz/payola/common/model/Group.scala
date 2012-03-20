package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

trait Group extends NamedEntity with OwnedEntity
{
    protected val _members: mutable.Seq[UserType]

    def members: immutable.Seq[UserType] = _members.toList
}
