package cz.payola.domain.entities

import collection.mutable._
import cz.payola._
import generic.{ConcreteEntity, SharedAnalysesOwner, ConcreteOwnedEntity, ConcreteNamedEntity}

class Group(
        id:String = java.util.UUID.randomUUID.toString,
        protected var _name: String,
        protected val _owner: User,
        validate: Boolean = true)
    extends ConcreteEntity(id)
    with common.entities.Group
    with ConcreteNamedEntity
    with ConcreteOwnedEntity
    with SharedAnalysesOwner
{
    def this() = this(null, null, null)

    protected val _members: ArrayBuffer[UserType] = new ArrayBuffer[UserType]()

    // We need to be able to create a new instance of Group with no parameters.
    // Hence if both _name and _owner are null, let it slip. If one of them is null
    // and the other isn't, something went wrong.

    /* Squeril is BIT*CH
    if (_name != null && _owner == null){
        throw new IllegalArgumentException("Group needs and owner!")
    }else if (_name == null && _owner != null){
        throw new IllegalArgumentException("Group needs a name!")
    }

    if (_owner != null) {
        _owner.addOwnedGroup(this)
    }
    */

    /** Adds a member to the group. Does nothing if already a member.
      *
      * '''Note''': Automatically adds this group to the user's groups.
      *
      * @param u The user to be added.
      *
      * @throws IllegalArgumentException if the user is null.
      */
    def addMember(u: User) = {
        require(u != null, "User is NULL!")

        if (!_members.contains(u)) {
            _members += u

            u.addToGroup(this)
        }
    }

    /** Results in true if the user is a member.
      *
      * @param u The user.
      *
      * @return True or false.
      */
    def hasMember(u: User): Boolean = _members.contains(u)

    /** Returns a user at index. Will raise an exception if the index is out of bounds.
      * The user will be loaded from DB if necessary.
      *
      * @param index Index of the user (according to the MemberIDs).
      * @return The group.
      */
    def memberAtIndex(index: Int): User = {
        require(index >= 0 && index < memberCount, "Member index out of bounds - " + index)
        _members(index)
    }

    /** Returns number of members. Doesn't include the owner.
      *
      * @return Number of members.
      */
    def memberCount: Int = _members.size

    /** Removes user from members.
      *
      * Note: Automatically removes the group from the user's groups.
      *
      * @param u The user to be removed.
      *
      * @throws IllegalArgumentException if the user is null or owner.
      */
    def removeMember(u: User) = {
        require(u != null, "User is NULL!")

        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_members.contains(u)) {
            u.removeFromGroup(this)

            _members -= u
        }
    }
}

