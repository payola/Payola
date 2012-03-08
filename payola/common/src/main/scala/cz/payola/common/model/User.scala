package cz.payola.common.model

trait User extends NamedModelObject {
    var email: String
    var password: String

    val memberGroups: List[Group] // Unordered
    val ownedGroups: List[Group] // Unordered


    def addToGroup(g: Group): Unit // As a member
    def addOwnedGroup(g: Group) // As an owner
    def isMemberOfGroup(g: Group): Boolean = g.hasMember(this)
    def isOwnerOfGroup(g: Group): Boolean = g.owner == this
    def memberGroupAtIndex(index: Int): Group
    def numberOfMemberGroups: Int
    def numberOfOwnedGroups: Int
    def ownedGroupAtIndex(index: Int): Group
    def removeFromGroup(g: Group): Unit // As a member
    def removeOwnedGroup(g: Group) // As an owner
    
    
}
