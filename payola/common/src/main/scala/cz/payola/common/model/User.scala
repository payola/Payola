package cz.payola.common.model

trait User extends NamedModelObject {
    var email: String
    var password: String

    def memberGroups: List[Group]
    def ownedGroups: List[Group]

    def ownedAnalyses: List[Analysis]
    def sharedAnalyses: List[AnalysisShare]


    def addAnalysis(a: Analysis)
    def addAnalysisShare(a: AnalysisShare)
    def addToGroup(g: Group): Unit // As a member
    def addOwnedGroup(g: Group) // As an owner
    def hasAccessToAnalysis(a: Analysis): Boolean
    def isMemberOfGroup(g: Group): Boolean = g.hasMember(this)
    def isOwnerOfAnalysis(a: Analysis): Boolean = a.owner == this
    def isOwnerOfGroup(g: Group): Boolean = g.owner == this
    def memberGroupAtIndex(index: Int): Group
    def numberOfMemberGroups: Int
    def numberOfOwnedAnalyses: Int
    def numberOfOwnedGroups: Int
    def numberOfSharedAnalyses: Int
    def ownedAnalysisAtIndex(index: Int): Analysis
    def ownedGroupAtIndex(index: Int): Group
    def removeFromGroup(g: Group): Unit // As a member
    def removeOwnedAnalysis(a: Analysis)
    def removeOwnedGroup(g: Group) // As an owner
    def removeSharedAnalysis(a: AnalysisShare)
    def sharedAnalysisAtIndex(index: Int): AnalysisShare
    
}
