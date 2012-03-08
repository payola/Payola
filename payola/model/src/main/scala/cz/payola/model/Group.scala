package cz.payola.model

import collection.mutable._
import generic.ConcreteNamedModelObject

class Group (nameStr: String, user: User) extends ConcreteNamedModelObject(nameStr) with cz.payola.common.model.Group {

    setOwner(user)

    // Shared analysis. Initially only IDs are loaded, actual shares are loaded from the
    // data layer as needed
    private val _sharedAnalysesIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _cachedAnalysisShares: HashMap[String, AnalysisShare] = new HashMap[String, AnalysisShare]()
    
    // Members. Initially only IDs are loaded, actual members are loaded from the
    // data layer as needed
    private val _memberIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _members: HashMap[String, User] = new HashMap[String, User]()

    user.addOwnedGroup(this)


    /** Adds an analysis share to the group.
      *
      * @param a The share.
      *
      * @throws IllegalArgumentException if the analysis share is null.
      */
    def addAnalysis(a: AnalysisShare) = {
        require(a != null, "Cannot share null analysis share")
        if (!_sharedAnalysesIDs.contains(a.objectID)){
            _sharedAnalysesIDs += a.objectID
            _cachedAnalysisShares.put(a.objectID, a)
        }
    }

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
    def analyses: List[AnalysisShare] = {
        val analyses = List[AnalysisShare]()
        _sharedAnalysesIDs foreach { shareID: String =>
            val a: Option[AnalysisShare] = _cachedAnalysisShares.get(shareID)
            if (a.isEmpty){
                // TODO loading from DB
            }else{
                a.get :: analyses
            }
        }
        analyses.reverse
    }

    /** Returns an analysis share at index. Will raise an exception if the index is out of bounds.
      * The analysis share will be loaded from DB if necessary.
      *
      * @param index Index of the analysis share (according to the AnalysesIDs).
      * @return The analysis share.
      */
    def analysisAtIndex(index: Int): AnalysisShare = {
        require(index >= 0 && index < numberOfAnalysis, "Shared analysis index out of bounds - " + index)
        val opt: Option[AnalysisShare] = _cachedAnalysisShares.get(_sharedAnalysesIDs(index))
        if (opt.isEmpty){
            // TODO Load from DB
            null
        }else{
            opt.get
        }
    }

    /** Returns true if this particular share has been shared with this group.
     *
     * @param share The share.
     * 
     * @return Returns true if this particular share has been shared with this group.
     */
    def containsAnalysisShare(share: AnalysisShare): Boolean = _sharedAnalysesIDs.contains(share.objectID)

    /** Results in true if this group has the analysis shared.
     *
     * @param a Analysis.
     *
     * @return True or false.
     */
    def hasAccessToAnalysis(a: Analysis): Boolean = analyses.exists(_.analysis == a)

    /** Results in true if the user is a member.
     *
     * @param u The user.
     *
     * @return True or false.
     */
    def hasMember(u: User): Boolean = _memberIDs.contains(u.objectID)


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
                u.get :: users
            }
        }
        users.reverse
    }

    /** Number of shared analyses.
      *
      * @return Number of shared analyses.
      */
    def numberOfAnalysis: Int = _sharedAnalysesIDs.size

    /** Sets the owner.
     *
     * @param u The owner.
     *
     * @throws IllegalArgumentException if the new user is null.
     */
    def owner_=(u: User) = {
        // Owner mustn't be null
        require(u != null)

        val oldOwner = owner
        _owner = u

        // Update relations
        u.addOwnedGroup(this)
        if (oldOwner != null) {
            oldOwner.removeOwnedGroup(this)
        }
    }

    /** Removes the passed analysis share from the group's analysis shares.
      *
      * @param a Analysis share to be removed.
      *
      * @throws IllegalArgumentException if the analysis is null.
      */
    def removeAnalysis(a: Analysis) = {
        require(a != null, "Cannot remove null analysis!")

        _sharedAnalysesIDs -= a.objectID
        _cachedAnalysisShares.remove(a.objectID)
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

}

