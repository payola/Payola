package cz.payola.model

import generic.{SharedAnalysesOwner, ConcreteNamedEntity}
import scala.collection.mutable._

class User(protected var _name: String) extends cz.payola.common.model.User with ConcreteNamedEntity with
    SharedAnalysesOwner {
    type GroupType = Group

    type AnalysisType = Analysis

    type AnalysisShareType = AnalysisShare

    protected var _email: String = ""

    protected var _password: String = ""

    // Analysis owned by the user and analysis that are shared directly to the user
    // To support lazy-loading, only AnalysesIDs are filled at first and when requesting
    // a particular analysis, it is loaded and stored in the HashMap cache.
    private val _ownedAnalysesIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

    private val _cachedAnalyses: HashMap[String, AnalysisType] = new HashMap[String, AnalysisType]()

    // Groups owned by the user and groups the user is a member in
    // To support lazy-loading, only GroupIDs are filled at first and when requesting
    // a particular group, it is loaded and stored in the HashMap cache.
    private val _ownedGroupIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

    private val _memberGroupIDs: ArrayBuffer[String] = new ArrayBuffer[String]()

    private val _cachedGroups: HashMap[String, Group] = new HashMap[String, Group]()

    protected val _ownedGroups: Seq[GroupType] = new ArrayBuffer[GroupType]()

    protected val _memberGroups: Seq[GroupType] = new ArrayBuffer[GroupType]()

    protected val _ownedAnalyses: Seq[AnalysisType] = new ArrayBuffer[AnalysisType]()

    /** Internal method which creates List of groups from IDs. It uses the user's cache
      * as well as loading from the data layer if the group hasn't been cached yet.
      *
      * @param ids An array of group IDs.
      * @return List of groups.
      */
    private def _groupsWithIDs(ids: ArrayBuffer[String]): List[Group] = {
        val groups = List[Group]()
        ids foreach {groupID =>
            val g: Option[Group] = _cachedGroups.get(groupID)
            if (g.isEmpty) {
                // TODO loading from DB
            } else {
                g.get :: groups
            }
        }
        groups.reverse
    }

    /** Adds the analysis to the analyses array. Does nothing if the analysis
      * has been already added. The Analysis has to be owned by the user.
      *
      * @param a Analysis to be added.
      *
      * @throws IllegalArgumentException if the analysis is null or the user isn't an owner of it.
      */
    def addAnalysis(a: AnalysisType) = {
        require(a != null, "Analysis mustn't be null")
        require(isOwnerOfAnalysis(a), "User must be owner of the analysis")
        if (!_ownedAnalysesIDs.contains(a.id)) {
            _ownedAnalysesIDs += a.id
            _cachedAnalyses.put(a.id, a)
        }
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
        if (!_memberGroupIDs.contains(g.id)) {
            _memberGroupIDs += g.id
            _cachedGroups.put(g.id, g)

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
        if (!_ownedGroupIDs.contains(g.id)) {
            _ownedGroupIDs += g.id
            _cachedGroups.put(g.id, g)
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
    def hasAccessToAnalysis(a: AnalysisType): Boolean = {
        if (_ownedAnalysesIDs.contains(a.id) || sharedAnalyses.exists(_.analysis.id == a.id)) {
            true
        } else {
            memberGroups.exists(_.hasAccessToSharedAnalysis(a)) ||
                ownedGroups.exists(_.hasAccessToSharedAnalysis(a))
        }
    }

    def isMemberOfGroup(g: Group): Boolean = g.hasMember(this)

    def isOwnerOfAnalysis(a: AnalysisType): Boolean = a.owner.id == this.id

    def isOwnerOfGroup(g: Group): Boolean = g.owner.id == this.id

    /** Returns a group at index. Will raise an exception if the index is out of bounds.
      * The group will be loaded from DB if necessary.
      *
      * @param index Index of the group (according to the GroupIDs).
      * @return The group.
      */
    def memberGroupAtIndex(index: Int): Group = {
        require(index >= 0 && index < memberGroupCount, "Member group index out of bounds - " + index)
        val opt: Option[Group] = _cachedGroups.get(_memberGroupIDs(index))
        if (opt.isEmpty) {
            // TODO Load from DB
            null
        } else {
            opt.get
        }
    }

    /** Number of member groups.
      *
      * @return Number of member groups
      */
    def memberGroupCount: Int = _memberGroupIDs.size

    /** Result is a new List consisting of only groups that
      *  the user is a member of.
      *
      * @return New List with groups that the user is a member of.
      */
    //def memberGroups = _groupsWithIDs(_memberGroupIDs)

    /** Returns a list of analyses owned by this user. Analyses will
      * be fetched from DB if necessary.
      *
      * @return List of owned analyses.
      */
    /*def ownedAnalyses = {
        val analyses = List[AnalysisType]()
        _ownedAnalysesIDs foreach { analysisID: String =>
            val a: Option[AnalysisType] = _cachedAnalyses.get(analysisID)
            if (a.isEmpty){
                // TODO loading from DB
            }else{
                a.get :: analyses
            }
        }
        analyses.reverse
    }*/

    /** Returns an analysis at index. Will raise an exception if the index is out of bounds.
      * The analysis will be loaded from DB if necessary.
      *
      * @param index Index of the analysis (according to the AnalysesIDs).
      * @return The analysis.
      */
    def ownedAnalysisAtIndex(index: Int): AnalysisType = {
        require(index >= 0 && index < ownedAnalysisCount, "Owned analysis index out of bounds - " + index)
        val opt: Option[AnalysisType] = _cachedAnalyses.get(_ownedAnalysesIDs(index))
        if (opt.isEmpty) {
            // TODO Load from DB
            null
        } else {
            opt.get
        }
    }

    /** Number of owned analyses.
      *
      * @return Number of owned analyses.
      */
    def ownedAnalysisCount: Int = _ownedAnalysesIDs.size

    /** Returns a group at index. Will raise an exception if the index is out of bounds.
      * The group will be loaded from DB if necessary.
      *
      * @param index Index of the group (according to the GroupIDs).
      * @return The group.
      */
    def ownedGroupAtIndex(index: Int): Group = {
        require(index >= 0 && index < ownedGroupCount, "Owned group index out of bounds - " + index)
        val opt: Option[Group] = _cachedGroups.get(_ownedGroupIDs(index))
        if (opt.isEmpty) {
            // TODO Load from DB
            null
        } else {
            opt.get
        }
    }

    /** Number of owned groups.
      *
      * @return Number of owned groups
      */
    def ownedGroupCount: Int = _ownedGroupIDs.size

    /** Result is a new List consisting of only groups that
      *  are owned by the user.
      *
      * @return New List with groups owned by the user.
      */
    //def ownedGroups = _groupsWithIDs(_ownedGroupIDs)

    /** Removes the user from the group.
      *
      * '''Note:''' This also removes the user from the group's member array.
      *
      * @param g Group to be removed.
      *
      * @return Nothing, needs to have a declared return type because it calls
      *          removeMember on the group which then may call back removeFromGroup
      *          back on the user.
      *
      * @throws IllegalArgumentException if the group is null.
      */
    def removeFromGroup(g: Group): Unit = {
        require(g != null, "Group is NULL!")

        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_memberGroupIDs.contains(g.id)) {
            _memberGroupIDs -= g.id
            _cachedGroups.remove(g.id)
            g.removeMember(this)
        }
    }

    /** Removes the passed analysis from the analyses owned by the user.
      *
      * @param a Analysis to be removed.
      *
      * @throws IllegalArgumentException if the analysis is null.
      */
    def removeOwnedAnalysis(a: AnalysisType) = {
        require(a != null, "Cannot remove null analysis!")

        _ownedAnalysesIDs -= a.id
        _cachedAnalyses.remove(a.id)
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

        _ownedGroupIDs -= g.id
        _cachedGroups.remove(g.id)
    }
}
