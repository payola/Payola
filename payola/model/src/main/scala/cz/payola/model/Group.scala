package cz.payola.model

import collection.mutable._
import generic.ConcreteNamedModelObject

class Group (nameStr: String, user: User) extends ConcreteNamedModelObject(nameStr) with cz.payola.common.model.Group{
    // Shared analysis
    private val _sharedAnalyses: ArrayBuffer[AnalysisShare] = new ArrayBuffer[AnalysisShare]()
    
    // Members. Initially only IDs are loaded, actual members are loaded from the
    // data layer as needed
    private val _memberIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _members: HashMap[String, User] = new HashMap[String, User]()

    // Group owner
    private var _owner: User = null
    setOwner(user)

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

        if (!_memberIDs.contains(u.objectID)){
            _memberIDs += u.objectID
            _members.put(u.objectID, u)

            u.addToGroup(this)
        }
    }

    /** Returns an immutable array of analysis shared with this group.
     *
     * @return An immutable array of analysis shared with this group.
     */
    def analyses: Array[AnalysisShare] = _sharedAnalyses.toArray

    /** Returns true if this particular share has been shared with this group.
     *
     * @param share The share.
     * 
     * @return Returns true if this particular share has been shared with this group.
     */
    def containsAnalysisShare(share: AnalysisShare): Boolean = _sharedAnalyses.contains(share)

    /** Results in true if this group has the analysis shared.
     *
     * @param a Analysis.
     *
     * @return True or false.
     */
    def hasAccessToAnalysis(a: Analysis): Boolean = _sharedAnalyses.exists(_.analysis == a)

    /** Results in true if the user is a member.
     *
     * @param u The user.
     *
     * @return True or false.
     */
    def hasMember(u: User): Boolean = _memberIDs.contains(u.objectID)

    /** Results in true if the user is this group's owner.
     *
     * @param u The user.
     *
     * @return True or false.
     */
    def isOwnedByUser(u: User): Boolean = _owner == u

    /** Returns a user at index. Will raise an exception if the index is out of bounds.
      * The user will be loaded from DB if necessary.
      *
      * @param index Index of the user (according to the MemberIDs).
      * @return The group.
      */
    def memberAtIndex(index: Int): User = {
        require(index >= 0 && index < numberOfMembers, "Member index out of bounds - " + index)
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
    def numberOfMembers: Int = _memberIDs.size

    /** Returns an immutable array of group members.
     *
     * @return An immutable array of group members.
     */
    def members: List[User] = {
        var users = List[User]()
        _memberIDs foreach { userID =>
            val u: Option[User] = _members.get(userID)
            if (u.isEmpty){
                // TODO loading from DB
            }else{
                users :: u.get
            }
        }
        users
    }

    /** Returns the owner.
     * 
     *  @return Group owner.
     */
    def owner: User = _owner

    /** Sets the owner.
     *
     * @param u The owner.
     *
     * @throws IllegalArgumentException if the new user is null.
     */
    def owner_=(u: User) = {
        // Owner mustn't be null
        require(u != null)

        val oldOwner = _owner
        _owner = u

        // Update relations
        u.addOwnedGroup(this)
        if (oldOwner != null) {
            oldOwner.removeOwnedGroup(this)
        }
    }

    /** Removes user from members.<br/>
     * <br/>
     * <strong>Note:</strong> Automatically removes the group from the user's groups.
     *
     *  @param u The user to be removed.
     *
     *  @throws IllegalArgumentException if the user is null or owner.
     */
    def removeMember(u: User) = {
        require(u != null, "User is NULL!")
        
        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_memberIDs.contains(u.objectID)){
            u.removeFromGroup(this)

            _memberIDs -= u.objectID
            _members.remove(u.objectID)
        }
    }

    /** Convenience method that just calls owner_=.
     *
     * @param u The new owner.
     *
     * @throws IllegalArgumentException if the user is null.
     */
     def setOwner(u: User) = owner_=(u);
}

