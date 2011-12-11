package cz.payola.model

import scala.collection.mutable._

class User(n: String) {
    private var _name: String = null
    setName(n)
    
    // Possibly the following two fields should private and
    // we should return an immutable copy from a method below?

    // Analysis owned by the user
    private val _ownedAnalyses: ArrayBuffer[Analysis] = new ArrayBuffer[Analysis]()
    // Analysis shared to the user
    private val _sharedAnalyses: ArrayBuffer[AnalysisShare] = new ArrayBuffer[AnalysisShare]()


    // Groups owned by the user
    private val _ownedGroups: ArrayBuffer[Group] = new ArrayBuffer[Group]()
    // Groups that the user is a member of
    private val _memberGroups: ArrayBuffer[Group] = new ArrayBuffer[Group]()


   /** Adds the analysis to the analyses array. Does nothing if the analysis
     * has been already added. The Analysis has to be owned by the user.
     *
     * @param a Analysis to be added.
    *
    *  @throws IllegalArgumentException if the analysis is null or the user isn't an owner of it.
     */
    def addAnalysis(a: Analysis) = {
        require(a != null, "Analysis mustn't be null")
        require(isOwnerOfAnalysis(a), "User must be owner of the analysis")
        if (!_ownedAnalyses.contains(a))
            _ownedAnalyses += a
    }

    /** Adds an analysis share to the user.
     *
     * @param a The share.
     *
     * @throws IllegalArgumentException if the analysis share is null.
     */
    def addAnalysisShare(a: AnalysisShare) = {
        require(a != null, "Cannot share null analysis share")
        if (!_sharedAnalyses.contains(a))
            _sharedAnalyses += a
    }

   /** Adds the group to the member group array. Does nothing if the group has already been added.
     *
     * @note This method automatically adds the current user as a member
     *       of the group if the user isn't.
     *
     * @param g Group to be added.
     *
     * @throws IllegalArgumentException if the group is null.
     */
    def addToGroup(g: Group): Unit = {
        require(g != null, "Group is NULL!")

        // Avoid double membership
        if (!_memberGroups.contains(g)) {
            _memberGroups += g
            g.addMember(this)
        }
    }

    /** Adds the group to the owned groups. The user '''must''' already be set as the group's owner.
     *
     * @param g Group to be added.
     *
     * @throws IllegalArgumentException if the group is null or the user isn't an owner of that group.
     */
    def addOwnedGroup(g: Group) = {
        require(g != null, "Group is NULL!")
        require(g.isOwnedByUser(this), "Group isn't owned by this user!")

        // Avoid double membership
        if (!_ownedGroups.contains(g))
            _ownedGroups += g
    }

   /** Results in true if the user has access to that particular analysis.
     * This method checks analyses owned by the user, analyses shared to him
     * as well as analyses shared to the groups he's a member or owner of.
     *
     * @param a The analysis about which we want to get the access privileges.
     *
     * @return True or false.
     */
    def hasAccessToAnalysis(a: Analysis): Boolean = {
        if (_ownedAnalyses.contains(a) || _sharedAnalyses.exists(_.analysis == a)) {
            true
        } else {
            _memberGroups.exists(_.hasAccessToAnalysis(a)) ||
                _ownedGroups.exists(_.hasAccessToAnalysis(a))
        }
    }

   /** Results in true if the user is a member of the group.
     *
     * @param g The group.
     *
     * @return True or false.
     */
    def isMemberOfGroup(g: Group): Boolean = _memberGroups.contains(g)

    /** Results in true if the user is an owner of the analysis.
     *
     * @param a The analysis.
     *
     * @return True or false.
     */
    def isOwnerOfAnalysis(a: Analysis): Boolean = a.owner == this

    /** Results in true is the user is an owner of the group.
     *
     * @param g The group.
     *
     * @return True or false.
     */
    def isOwnerOfGroup(g: Group): Boolean = g.isOwnedByUser(this)

    /** Result is a new Array consisting of only groups that
     *  the user is a member of.
     *
     *  @return New Array with groups that the user is a member of.
     */
    def memberGroups: Array[Group] = _memberGroups.toArray

    /** Returns the user's name.
     *
     * @return User's name.
     */
    def name: String = _name

    /** Sets the user's name.
     *
     * @param n New name.
     *
     * @throws IllegalArgumentException if the new name is null or empty.
     */
    def name_=(n: String) = {
        // The name mustn't be null and mustn't be empty
        require(n != null && n != "")

        _name = n
    }

    /** Result is a new Array consisting of only groups that
     *  are owned by the user.
     *
     *  @return New Array with groups owned by the user.
     */
    def ownedGroups: Array[Group] = _ownedGroups.toArray

    /** Removes the passed analysis from the analyses owned by the user.
     *
     * @param a Analysis to be removed.
     *
     * @throws IllegalArgumentException if the analysis is null.
     */
    def removeAnalysis(a: Analysis) = {
        require(a != null, "Cannot remove null analysis!")
        _ownedAnalyses -= a
    }

    /** Removes the passed analysis from the analyses shared to the user.
     *
     * @param a Analysis share to be removed.
     *
     * @throws IllegalArgumentException if the analysis share is null.
     */
    def removeAnalysisShare(a: AnalysisShare) = {
        require(a != null, "Cannot remove null analysis!")
        _sharedAnalyses -= a
    }

    /** Removes the user from the group.
     *
     *  '''Note:''' This also removes the user from the group's member array.
     *
     *  @param g Group to be removed.
     *
     *  @return Nothing, needs to have a declared return type because it calls
     *          removeMember on the group which then may call back removeFromGroup
     *          back on the user.
     *
     *  @throws IllegalArgumentException if the group is null.
     */
    def removeFromGroup(g: Group): Unit = {
        require(g != null, "Group is NULL!")

        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_memberGroups.contains(g)){
            _memberGroups -= g
            g.removeMember(this)
        }
    }

    /** Removes the group from the user's list of owned groups. The user '''mustn't''' be the
     * group's owner anymore.
     *
     * @param g Group to be removed.
     *
     * @throws IllegalArgumentException if the group is null or the user is still owner of the group.
     */
    def removeOwnedGroup(g: Group) = {
        require(g != null, "Group is NULL!")
        require(!g.isOwnedByUser(this), "Group is still owned by this user!")

        _ownedGroups -= g
    }

    /** Convenience method that just calls name_=.
     *
     * @param n The new user's name.
     *
     * @throws IllegalArgumentException if the new name is null or empty.
     */
    def setName(n: String) = name_=(n);



}
