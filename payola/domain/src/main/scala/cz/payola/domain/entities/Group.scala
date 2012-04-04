package cz.payola.domain.entities

import collection.mutable._
import cz.payola._
import generic.{ConcreteEntity, SharedAnalysesOwner, ConcreteOwnedEntity, ConcreteNamedEntity}

class Group(id:String, protected var _name: String, protected val _owner: User)
    extends ConcreteEntity(id)
    with common.entities.Group
    with ConcreteNamedEntity
    with ConcreteOwnedEntity
    with SharedAnalysesOwner
{
    // Members. Initially only IDs are loaded, actual members are loaded from the
    // data layer as needed
    private val _memberIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

    protected val _members: ArrayBuffer[UserType] = new ArrayBuffer[UserType]()

    /*TODO: _owner can be null when creating instance without parameters
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

        if (!_memberIDs.contains(u.id)) {
            _memberIDs += u.id
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
    def hasMember(u: User): Boolean = _memberIDs.contains(u.id)

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
    def memberCount: Int = _memberIDs.size

    /** Returns an immutable array of group members.
      *
      * @return An immutable array of group members.
      */
    /*def members = {
        val users = List[User]()
        _memberIDs foreach { userID =>
            val u: Option[User] = _members.get(userID)
            if (u.isEmpty){
                // TODO loading from DB
            }else{
                u.get :: users
            }
        }
        users.reverse
    }*/

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
        if (_memberIDs.contains(u.id)) {
            u.removeFromGroup(this)

            _memberIDs -= u.id
            _members -= u
        }
    }
}

