package cz.payola.domain.entities

/**
  * @param _name Name of the group.
  * @param _owner Owner of the group.
  */
class Group(protected var _name: String, protected var _owner: User)
    extends Entity
    with NamedEntity
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
      * @throws IllegalArgumentException if the user is null, owner of the group or already member of the group.
      */
    def addMember(user: UserType) {
        require(user != null, "The user mustn't be null.")
        require(!members.contains(user), "The user is already member of the group.")
        require(user != owner, "The user mustn't be the group owner.")

        storeMember(user)
    }

    /**
      * Returns whether the specified user is a member of the group.
      * @param user The user to check.
      */
    def hasMember(user: UserType): Boolean = {
        members.contains(user)
    }

    /**
      * Removes the specified user from the group members.
      * @param user The user to be removed.
      * @return The removed member.
      */
    def removeMember(user: UserType): Option[UserType] = {
        require(user != null, "The user mustn't be null.")
        ifContains(members, user) {
            discardMember(user)
        }
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

