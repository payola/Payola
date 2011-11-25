package cz.payola.model

/**
 * User: Krystof Vasa
 * Date: 21.11.11
 * Time: 10:57
 */

import scala.collection.mutable._

class User(val name: String) {
    // The name mustn't be null and musn't be empty
    // TODO: make sure it's unique system-wide?
    assert(name != null && name != "")

    // Possibly the following two fields should private and
    // we should return an immutable copy from a method below?

    // Analysis owned by the user or shared directly to him
    val _analyses: ArrayBuffer[Analysis] = new ArrayBuffer[Analysis]()
    // Groups owned by the user or shared to him
    // TODO: Rozdelit do dvou
    val _groups: ArrayBuffer[Group] = new ArrayBuffer[Group]()


   /** Adds the analysis to the analyses array. Does nothing if the analysis
     * has been already added.
     *
     * @param a Analysis to be added.
     */
    def addAnalysis(a: Analysis) = {
        if (_analyses.contains(a) == false)
            _analyses += a
    }

   /** Adds the group to the group array. Does nothing if the group has
     * already been added.
     *
     * @note This method automatically adds the current user as a member
     *       of the group.
     *
     * @param g Group to be added.
     */
    def addToGroup(g: Group): Unit = {
        assert(g != null, "Group is NULL!")

        // Avoid double membership
        if (_groups.contains(g) == false) {
            _groups += g
            g.addMember(this)
        }
    }

   /** Results in true if the user has access to that particular analysis.
     * Both the user's analysis array and each group's analysis array are
     * checked.
     *
     * @param a The analysis about which we want to get the access privileges.
     *
     * @return True or false.
     */
    def hasAccessToAnalysis(a: Analysis): Boolean = {
        if (_analyses.contains(a)) {
            true
        } else {
            _groups.exists(_.hasAccessToAnalysis(a))
        }
    }

   /** Results in true if the user is a member of the group.
     *
     * @param g The group.
     *
     * @return True or false.
     */
    def isMemberOfGroup(g: Group): Boolean = _groups.contains(g)

    /** Results in true if the user is an owner of the analysis.
     *
     * @param a The analysis.
     *
     * @return True or false.
     */
    def isOwnerOfAnalysis(a: Analysis): Boolean = a.isOwnedByUser(this)

    /** Results in true is the user is an owner of the group.
     *
     * @param g The group.
     *
     * @return True or false.
     */
    def isOwnerOfGroup(g: Group): Boolean = g.isOwnedByUser(this)

    /** Result is a new ArrayBuffer consisting of only groups that
     *  are owned by the user.
     *
     *  @return New ArrayBuffer with groups owned by the user.
     */
    def ownedGroups: ArrayBuffer[Group] = {
        _groups.filter(_.isOwnedByUser(this))
    }

    /** Removes the passed analysis from the users analyses array.
     *
     * @param a Analysis to be removed.
     */
    def removeAnalysis(a: Analysis) = _analyses -= a

    /** Removes the user from the group.
     *
     *  '''Note:''' 1) Will result in exception if you're removing the owner.
     *          To remove an owner, first change the owner to someone else.
     *       2) This also removes the user from the group's member array.
     *
     *  @param g Group to be removed.
     *
     *  @return Nothing, needs to have a declared return type because it calls
     *          removeMember on the group which then may call back removeFromGroup
     *          back on the user.
     */
    def removeFromGroup(g: Group): Unit = {
        assert(g != null, "Group is NULL!")
        assert(!g.isOwnedByUser(this), "Group is owned by this user!")
        
        // Need to make this check, otherwise we'd
        // get in to an infinite cycle
        if (_groups.contains(g)){
            _groups -= g
            g.removeMember(this)
        }
    }



}
