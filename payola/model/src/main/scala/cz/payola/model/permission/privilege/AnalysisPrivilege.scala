package cz.payola.model.permission.privilege

import cz.payola.model.Analysis
import cz.payola.model.permission.action.Action

abstract class AnalysisPrivilege[T <: Action[_]](a: Analysis) extends Privilege[T, Analysis](a) {

}
