package cz.payola.common.model

trait Group extends NamedModelObject {
    var owner: User

    val members: List[User] // Doesn't contain owner, unordered

    def addMember(u: User)
    def hasMember(u: User): Boolean
    def isOwnedByUser(u: User): Boolean
    def memberAtIndex(index: Int): User
    def numberOfMembers: Int
    def removeMember(u: User)
}
