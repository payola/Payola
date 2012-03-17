package cz.payola.model.permission.action

import cz.payola.model.Analysis

object AnalysisModificationActionType extends Enumeration {
    type AnalysisModificationActionType = Value
    val ChangeName, ShareAnalysis, ShareAnalysisIncludingData, ModifyData, ModifyAnalysisConfiguration = Value
}

class AnalysisModificationAction(a: Analysis, val actionType: AnalysisModificationActionType.Value, val newValue: Any) extends Action[Analysis](a) {
}
