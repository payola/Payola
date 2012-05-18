package cz.payola.domain.entities

import permissions.privilege.{PublicPrivilege, GroupPrivilege, AnalysisPrivilege, Privilege}
import scala.collection.mutable
import cz.payola.domain.entities.analyses.DataSource

/** User entity at the domain level.
  *
  * Contains owned analyses and groups, member groups and privileges.
  *
  * @param _name Name of the user.
  */
class User(protected var _name: String)
    extends Entity
    with NamedEntity
    with cz.payola.common.entities.User
{
    checkConstructorPostConditions()

    type GroupType = Group

    type AnalysisType = Analysis

    type DataSourceType = DataSource

    type PrivilegeType = Privilege[_]

    protected var _email: String = ""

    protected var _password: String = ""

    protected val _ownedGroups = new mutable.ArrayBuffer[GroupType]()

    protected val _ownedAnalyses = new mutable.ArrayBuffer[AnalysisType]()

    protected val _ownedDataSources = new mutable.ArrayBuffer[DataSource]()

    protected val _privileges = new mutable.ArrayBuffer[PrivilegeType]()

    /** Goes through privileges and returns a sequence of Analysis objects to which
      * the user has some kind of access.
      *
      * @return Analysis with access.
      */
    def accessibleAnalyses: Seq[AnalysisType] = {
        privileges.collect { case p: AnalysisPrivilege => p.obj }.distinct
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
        require(a.owner.exists(_ == this), "User must be owner of the analysis")

        if (!_ownedAnalyses.contains(a)) {
            _ownedAnalyses += a
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
        require(g.owner == this, "Group isn't owned by this user!")

        // Avoid double membership
        if (!_ownedGroups.contains(g)) {
            _ownedGroups += g
        }
    }

    def isMemberOfGroup(g: Group): Boolean = g.hasMember(this)

    /** Result is a new List consisting of only groups that
      * the user is a member of.
      *
      * @return New List with groups that the user is a member of.
      */
    def memberGroups: collection.Seq[GroupType] = {
        privileges.collect { case p: GroupPrivilege => p.obj }.distinct
    }

    /** Returns an analysis at index. Will raise an exception if the index is out of bounds.
      * The analysis will be loaded from DB if necessary.
      *
      * @param index Index of the analysis (according to the AnalysesIDs).
      * @return The analysis.
      */
    def ownedAnalysisAtIndex(index: Int): AnalysisType = {
        require(index >= 0 && index < ownedAnalysisCount, "Owned analysis index out of bounds - " + index)
        _ownedAnalyses(index)
    }

    /** Number of owned analyses.
      *
      * @return Number of owned analyses.
      */
    def ownedAnalysisCount: Int = _ownedAnalyses.size

    /** Returns a group at index. Will raise an exception if the index is out of bounds.
      * The group will be loaded from DB if necessary.
      *
      * @param index Index of the group (according to the GroupIDs).
      * @return The group.
      */
    def ownedGroupAtIndex(index: Int): Group = {
        require(index >= 0 && index < ownedGroupCount, "Owned group index out of bounds - " + index)
        _ownedGroups(index)
    }

    /** Number of owned groups.
      *
      * @return Number of owned groups
      */
    def ownedGroupCount: Int = _ownedGroups.size

    /** Privileges. Only returns public ones.
      *
      * @return Public privileges.
      */
    def publicPrivileges: Seq[PrivilegeType] = {
        _privileges.collect { case p: PublicPrivilege => p }
    }

    /** Removes the passed analysis from the analyses owned by the user.
      *
      * @param a Analysis to be removed.
      *
      * @throws IllegalArgumentException if the analysis is null.
      */
    def removeOwnedAnalysis(a: AnalysisType) = {
        require(a != null, "Cannot remove null analysis!")

        _ownedAnalyses -= a
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
        require(g.owner != this, "Group is still owned by this user!")

        _ownedGroups -= g
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[User]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }
}
