package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.permission.action.Action

abstract class AnalysisPrivilege[T <: Action[_]](a: Analysis) extends Privilege[T, Analysis](a)
{
}
