package cz.payola.common.model

trait Group extends NamedModelObject with OwnedObject {
    def members: List[User] // Doesn't contain owner, unordered
    def sharedAnalyses: List[AnalysisShare]

    def addMember(u: User)
    def addSharedAnalysis(a: AnalysisShare)
    def containsSharedAnalysis(share: AnalysisShare): Boolean
    def hasAccessToSharedAnalysis(a: Analysis): Boolean
    def hasMember(u: User): Boolean
    def isOwnedByUser(u: User): Boolean = owner == u
    def memberAtIndex(index: Int): User
    def numberOfSharedAnalyses: Int
    def numberOfMembers: Int
    def removeSharedAnalysis(a: Analysis)
    def removeMember(u: User)
    def sharedAnalysisAtIndex(index: Int): AnalysisShare

}
