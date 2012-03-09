package cz.payola.common.model

trait AnalysisShare extends ModelObject {
    val SharePrivilegeResultOnly: Int = 1 << 0
    val SharePrivilegeIncludingData: Int = 1 << 1

    def analysis: Analysis
    var privilege: Int
}
