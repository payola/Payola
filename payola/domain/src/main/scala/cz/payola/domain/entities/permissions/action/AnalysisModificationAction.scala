package cz.payola.domain.permission.action

import cz.payola.domain.entities.Analysis

object AnalysisModificationActionType extends Enumeration
{
    type AnalysisModificationActionType = Value

    val ChangeName, ShareAnalysis, ShareAnalysisIncludingData, ModifyData, ModifyAnalysisConfiguration = Value
}

class AnalysisModificationAction(a: Analysis, val actionType: AnalysisModificationActionType.Value,
    val newValue: Any) extends Action[Analysis](a)
{
}
