package cz.payola.common.model

object SharePriviledges {
    val SharePrivilegeResultOnly: Int = 1 << 0
    val SharePrivilegeIncludingData: Int = 1 << 1
}

trait AnalysisShare extends ModelObject {
    def analysis: Analysis
    var privilege: Int
}
