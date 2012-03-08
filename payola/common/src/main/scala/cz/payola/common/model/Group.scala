package cz.payola.common.model

trait Group extends NamedModelObject with OwnedObject {
    def members: List[User] // Doesn't contain owner, unordered
    def analyses: List[AnalysisShare]

    def addAnalysis(a: AnalysisShare)
    def addMember(u: User)
    def analysisAtIndex(index: Int): AnalysisShare
    def containsAnalysisShare(share: AnalysisShare): Boolean
    def hasAccessToAnalysis(a: Analysis): Boolean
    def hasMember(u: User): Boolean
    def isOwnedByUser(u: User): Boolean = owner == u
    def memberAtIndex(index: Int): User
    def numberOfAnalysis: Int
    def numberOfMembers: Int
    def removeAnalysis(a: Analysis)
    def removeMember(u: User)
}
