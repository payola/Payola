package cz.payola.common.model

object SharePrivilege extends Enumeration {
    type SharePrivilege = Value
    val SharePrivilegeResultOnly, SharePrivilegeIncludingData = Value
}

import SharePrivilege._

trait AnalysisShare extends ModelObject {
    def analysis: Analysis
    var privilege: SharePrivilege
}
