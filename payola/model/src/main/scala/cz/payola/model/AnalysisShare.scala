package cz.payola.model

object SharePrivilege extends Enumeration {
    type SharePrivilege = Value
    val SharePrivilegeResultOnly, SharePrivilegeIncludingData = Value
}

import SharePrivilege._

class AnalysisShare (var analysis: Analysis, var privilege: SharePrivilege) {
    require(analysis != null, "Analysis cannot be null!")
    require(privilege != null, "Privilige cannot be null!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilegeResultOnly)
}
