package cz.payola.domain.entities

import permissions.privilege.PublicPrivilege
import scala.collection.mutable._
import cz.payola.domain.entities.generic.{SharedAnalysesOwner, ConcreteNamedEntity}
import cz.payola.domain.entities.permissions.privilege.{GroupPrivilege, AnalysisPrivilege, Privilege}

class User(protected var _name: String) extends cz.payola.common.entities.User with ConcreteNamedEntity with
SharedAnalysesOwner
{
    type GroupType = Group

    type AnalysisType = Analysis

    type PrivilegeType = Privilege[_]

    protected var _email: String = ""
    protected var _password: String = ""

    protected val _ownedAnalyses: ArrayBuffer[AnalysisType] = new ArrayBuffer[AnalysisType]()
    protected val _ownedGroups: ArrayBuffer[GroupType] = new ArrayBuffer[GroupType]()
    protected val _memberGroups: ArrayBuffer[GroupType] = new ArrayBuffer[GroupType]()
    protected val _privileges: ArrayBuffer[PrivilegeType] = new ArrayBuffer[PrivilegeType]()

    /** Goes through privileges and returns a sequence of Analysis objects to which
      * the user has some kind of access.
      *
      * @return Analysis with access.
      */
    def accessibleAnalyses: collection.Seq[AnalysisType] = {
        val as: ArrayBuffer[AnalysisType] = new ArrayBuffer[AnalysisType]()
        _privileges foreach { p: PrivilegeType =>
            if (p.isInstanceOf[AnalysisPrivilege]){
                val a: AnalysisType = p.asInstanceOf[AnalysisPrivilege].obj
                if (!as.contains(a)){
                    as += a
                }
            }
        }
        as
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

        if (!_ownedAnalyses.contains(a)) {
            _ownedAnalyses += a
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
        if (!_memberGroups.contains(g)) {
            _memberGroups += g

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
        if (!_ownedGroups.contains(g)) {
            _ownedGroups += g
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

        _memberGroups(index)
    }

    /** Number of member groups.
      *
      * @return Number of member groups
      */
    def memberGroupCount: Int = _memberGroups.size

    /** Result is a new List consisting of only groups that
      *  the user is a member of.
      *
      * @return New List with groups that the user is a member of.
      */
    def memberGroups = {
        val gs: ArrayBuffer[GroupType] = new ArrayBuffer[GroupType]()
        _privileges foreach { p: PrivilegeType =>
            if (p.isInstanceOf[GroupPrivilege]){
                val g: GroupType = p.asInstanceOf[GroupPrivilege].obj
                if (!gs.contains(g)){
                    gs += g
                }
            }
        }
        gs
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
    def privileges: collection.Seq[PrivilegeType] = _privileges filter { p: Privilege[_] => p.isInstanceOf[PublicPrivilege] }


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
        if (_memberGroups.contains(g)) {
            _memberGroups -= g
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
        require(!g.isOwnedByUser(this), "Group is still owned by this user!")

        _ownedGroups -= g
    }
}
