package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.permissions.action.Action

abstract class AnalysisPrivilege[T <: Action[Analysis]](a: Analysis) extends Privilege[T,  Analysis](a)
{
}
