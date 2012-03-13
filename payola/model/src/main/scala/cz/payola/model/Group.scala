package cz.payola.model

import collection.mutable._
import cz.payola._
import generic.{SharedAnalysesOwner, ConcreteOwnedEntity, ConcreteNamedEntity}
import cz.payola.scala2json.annotations._

@JSONUnnamedClass
class Group (nameStr: String, user: User) extends common.model.Group with ConcreteNamedEntity with ConcreteOwnedEntity with SharedAnalysesOwner
{
    setName(nameStr)
    setOwner(user)

    type AnalysisShareType = AnalysisShare

    // Members. Initially only IDs are loaded, actual members are loaded from the
    // data layer as needed
    @JSONFieldName(name = "members") private val _memberIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    @JSONTransient private val _members: HashMap[String, User] = new HashMap[String, User]()

    user.addOwnedGroup(this)

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

        if (!_memberIDs.contains(u.id)){
            _memberIDs += u.id
            _members.put(u.id, u)

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
        val opt: Option[User] = _members.get(_memberIDs(index))
        if (opt.isEmpty){
            // TODO Load from DB
            null
        }else{
            opt.get
        }
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
    def members = {
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
    }

    /** Sets the owner.
     *
     * @param u The owner.
     *
     * @throws IllegalArgumentException if the new user is null.
     */
    override def owner_=(u: User) = {
        // Owner mustn't be null
        require(u != null)

        val oldOwner = owner
        _owner = u
        _ownerID = u.id

        // Update relations
        u.addOwnedGroup(this)
        if (oldOwner != null) {
            oldOwner.removeOwnedGroup(this)
        }
    }

    /** Removes user from members.
     *
     * Note: Automatically removes the group from the user's groups.
     *
     *  @param u The user to be removed.
     *
     *  @throws IllegalArgumentException if the user is null or owner.
     */
    def removeMember(u: User) = {
        require(u != null, "User is NULL!")
        
        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_memberIDs.contains(u.id)){
            u.removeFromGroup(this)

            _memberIDs -= u.id
            _members.remove(u.id)
        }
    }

}

