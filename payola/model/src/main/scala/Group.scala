package cz.payola.model

import collection.mutable._

/**
 * User: Krystof Vasa
 * Date: 21.11.11
 * Time: 11:01
 */
class Group (nameStr: String, user: User){
    // Shared analysis
    private val _sharedAnalyses: ArrayBuffer[AnalysisShare] = new ArrayBuffer[AnalysisShare]()
    
    // Shared members. Add the owner to members automatically
    private val _members: ArrayBuffer[User] = new ArrayBuffer[User]()

    // Group owner
    private var _owner: User = null
    owner = user

    // Group name
    private var _name: String = null
    name = nameStr

    user.addOwnedGroup(this)


    /** Adds a member to the group. Does nothing if already a member.
     *
     * '''Note''': Automatically adds this group to the user's groups.
     *
     * @param u The user to be added.
     *
     * @throws AssertionError if the user is null.
     */
    def addMember(u: User) = {
        assert(u != null, "User is NULL!")
        if (!_members.contains(u)){
            _members += u
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
    def hasMember(u: User): Boolean = _members.contains(u)

    /** Results in true if the user is this group's owner.
     *
     * @param u The user.
     *
     * @return True or false.
     */
    def isOwnedByUser(u: User): Boolean = _owner == u

    /** Returns an immutable array of group members.
     *
     * @return An immutable array of group members.
     */
    def members: Array[User] = _members.toArray

    /** Returns the name of the group.
     *
     * @return Group name.
     */
    def name: String = _name

    /** Sets the group's name.
     *
     * @param The new name.
     *
     * @throws AssertionError if the new name is null or empty.
     */
    def name_=(n: String) = {
        // Name mustn't be null or empty
        assert(n != null && n != "")

        _name = n
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
     * @throws AssertionError if the new user is null.
     */
    def owner_=(u: User) = {
        // Owner mustn't be null
        assert(u != null)

        val oldOwner = _owner
        _owner = u

        // Update relations
        u.addOwnedGroup(this)
        if (oldOwner != null)
            oldOwner.removeOwnedGroup(this)
    }



    /** Removes user from members.<br/>
     * <br/>
     * <strong>Note:</strong> Automatically removes the group from the user's groups.
     *
     *  @param u The user to be removed.
     *
     *  @throws AssertionError if the user is null or owner.
     */
    def removeMember(u: User) = {
        assert(u != null, "User is NULL!")
        
        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_members.contains(u)){
            u.removeFromGroup(this)
            _members -= u
        }
    }

    /** Convenience method that just calls name_=.
     *
     * @param n The new group name.
     *
     * @throws AssertionError if the new name is null or empty.
     */
    def setName(n: String) = name_=(n);

    /** Convenience method that just calls owner_=.
     *
     * @param u The new owner.
     *
     * @throws AssertionError if the user is null.
     */
    private def setOwner(u: User) = owner_=(u);
}

