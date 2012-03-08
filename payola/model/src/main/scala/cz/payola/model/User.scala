package cz.payola.model

import generic.ConcreteNamedModelObject
import scala.collection.mutable._

class User(n: String) extends ConcreteNamedModelObject(n) with cz.payola.common.model.User {

    var email: String = ""
    var password: String = ""
    
    // Analysis owned by the user
    private val _ownedAnalyses: ArrayBuffer[Analysis] = new ArrayBuffer[Analysis]()
    // Analysis shared to the user
    private val _sharedAnalyses: ArrayBuffer[AnalysisShare] = new ArrayBuffer[AnalysisShare]()


    // Groups owned by the user and groups the user is a member in
    // To support lazy-loading, only GroupIDs are filled at first and when requesting
    // a particular group, it is loaded and stored in the HashMap cache.
    private val _ownedGroupIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _memberGroupIDs: ArrayBuffer[String] = new ArrayBuffer[String]()
    private val _cachedGroups: HashMap[String, Group] = new HashMap[String,Group]()


    /** Internal method which creates List of groups from IDs. It uses the user's cache
      * as well as loading from the data layer if the group hasn't been cached yet.
      *
      * @param ids An array of group IDs.
      * @return List of groups.
      */
    private def _groupsWithIDs(ids: ArrayBuffer[String]): List[Group] = {
        var groups = List[Group]()
        ids foreach { groupID =>
            val g: Option[Group] = _cachedGroups.get(groupID)
            if (g.isEmpty){
                // TODO loading from DB
            }else{
                groups :: g.get
            }
        }
        groups
    }
    
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
        require(g != null, "Cannot add a user to a null group!")

        // Avoid double membership
        if (!_memberGroupIDs.contains(g.objectID)) {
            _memberGroupIDs += g.objectID
            _cachedGroups.put(g.objectID, g)

            // Automatically add self to the group as well
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
        if (!_ownedGroupIDs.contains(g.objectID)){
            _ownedGroupIDs += g.objectID
            _cachedGroups.put(g.objectID, g)
        }

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



    /** Results in true if the user is an owner of the analysis.
     *
     * @param a The analysis.
     *
     * @return True or false.
     */
    def isOwnerOfAnalysis(a: Analysis): Boolean = a.owner == this

    /** Returns a group at index. Will raise an exception if the index is out of bounds.
      * The group will be loaded from DB if necessary.
      *
      * @param index Index of the group (according to the GroupIDs).
      * @return The group.
      */
    def memberGroupAtIndex(index: Int): Group = {
        require(index >= 0 && index < numberOfMemberGroups, "Member group index out of bounds - " + index)
        val opt: Option[Group] = _cachedGroups.get(_memberGroupIDs(index)) 
        if (opt.isEmpty){
            // TODO Load from DB
            null
        }else{
            opt.get
        }
    }
    
    /** Result is a new List consisting of only groups that
     *  the user is a member of.
     *
     *  @return New List with groups that the user is a member of.
     */
    def memberGroups: List[Group] = _groupsWithIDs(_memberGroupIDs)

    /** Number of member groups.
      *
      * @return Number of member groups
      */
    def numberOfMemberGroups: Int = _memberGroupIDs.size

    /** Number of owned groups.
      *
      * @return Number of owned groups
      */
    def numberOfOwnedGroups: Int = _ownedGroupIDs.size

    /** Returns a group at index. Will raise an exception if the index is out of bounds.
      * The group will be loaded from DB if necessary.
      *
      * @param index Index of the group (according to the GroupIDs).
      * @return The group.
      */
    def ownedGroupAtIndex(index: Int): Group = {
        require(index >= 0 && index < numberOfOwnedGroups, "Owned group index out of bounds - " + index)
        val opt: Option[Group] = _cachedGroups.get(_ownedGroupIDs(index))
        if (opt.isEmpty){
            // TODO Load from DB
            null
        }else{
            opt.get
        }
    }

    /** Result is a new List consisting of only groups that
     *  are owned by the user.
     *
     *  @return New List with groups owned by the user.
     */
    def ownedGroups: List[Group] = _groupsWithIDs(_ownedGroupIDs)

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
        if (_memberGroupIDs.contains(g.objectID)){
            _memberGroupIDs -= g.objectID
            _cachedGroups.remove(g.objectID)
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

        _ownedGroupIDs -= g.objectID
        _cachedGroups.remove(g.objectID)
    }





}
