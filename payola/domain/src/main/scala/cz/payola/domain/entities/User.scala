package cz.payola.domain.entities

import permissions.privilege.{PublicPrivilege, GroupPrivilege, AnalysisPrivilege, Privilege}
import cz.payola.domain.entities.analyses.DataSource
import scala.collection._

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

    /**
      * Adds the analysis to the users owned analyses. The analysis has to be owned by the user.
      * @param analysis Analysis to be added.
      * @throws IllegalArgumentException if the analysis is null, the user already owns it or isn't an owner of it.
      */
    def addOwnedAnalysis(analysis: AnalysisType) {
        require(analysis != null, "Analysis mustn't be null.")
        require(!ownedAnalyses.contains(analysis), "The analysis is already owned by the user.")
        require(analysis.owner.exists(_ == this), "User must be owner of the analysis.")

        storeOwnedAnalysis(analysis)
    }

    /**
      * Removes the specified analysis from the users owned analyses.
      * @param analysis The analysis to be removed.
      * @return The removed analysis.
      */
    def removeOwnedAnalysis(analysis: AnalysisType): Option[Analysis] = {
        require(analysis != null, "Analysis mustn't be null.")
        ifContains(ownedAnalyses, analysis) {
            discardOwnedAnalysis(analysis)
        }
    }

    /**
      * Adds the group to the users owned groups. The group has to be owned by the user.
      * @param group The group to be added.
      * @throws IllegalArgumentException if the group is null, the user already owns it or isn't an owner of it.
      */
    def addOwnedGroup(group: GroupType) {
        require(group != null, "Group mustn't be null.")
        require(!ownedGroups.contains(group), "The group is already owned by the user.")
        require(group.owner == this, "User must be owner of the group.")

        storeOwnedGroup(group)
    }

    /**
      * Removes the group from the users owned groups.
      * @param group The group to be removed.
      * @return The removed group.
      */
    def removeOwnedGroup(group: GroupType): Option[GroupType] = {
        require(group != null, "Group mustn't be null.")
        ifContains(ownedGroups, group) {
            discardOwnedGroup(group)
        }
    }

    /**
      * Returns the public privileges.
      */
    def publicPrivileges: Seq[PrivilegeType] = {
        privileges.collect {
            case p: PublicPrivilege => p
        }
    }

    /**
      * Returns the analyses that are accessible for the user.
      */
    def accessibleAnalyses: Seq[AnalysisType] = {
        privileges.collect {
            case p: AnalysisPrivilege => p.obj
        }.distinct
    }

    /**
      * Returns the groups the user is member of.
      */
    def memberGroups: Seq[GroupType] = {
        privileges.collect {
            case p: GroupPrivilege => p.obj
        }.distinct
    }

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[User]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
    }
}
