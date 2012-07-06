package cz.payola.domain.entities

import cz.payola.domain.Entity

/**
  * @param _name Name of the group.
  * @param _owner Owner of the group.
  */
class Group(protected var _name: String, protected var _owner: User)
    extends Entity
    with NamedEntity
    with PrivilegableEntity
    with cz.payola.common.entities.Group
{
    // If the group isn't in the owners owned groups yet, add it there (the group may already be there, because there
    // may be another instance with the same ID which is therefore considered identical to this instance).
    if (_owner != null && !_owner.ownedGroups.contains(this)) {
        _owner.addOwnedGroup(this)
    }
    checkConstructorPostConditions()

    type UserType = User

    /**
      * Adds a member to the group.
      * @param user The user to be added.
      */
    def addMember(user: UserType) {
        require(user != owner, "The user mustn't be the group owner.")
        addRelatedEntity(user, members, storeMember)
    }

    /**
      * Removes the specified user from the group members.
      * @param user The user to be removed.
      * @return The removed member.
      */
    def removeMember(user: UserType): Option[UserType] = {
        removeRelatedEntity(user, members, discardMember)
    }

    /**
      * Returns whether the specified user is a member of the group.
      * @param user The user to check.
      */
    def hasMember(user: UserType): Boolean = {
        members.contains(user)
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[Group]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        require(owner != null, "Owner of the entity mustn't be null.")
    }
}

