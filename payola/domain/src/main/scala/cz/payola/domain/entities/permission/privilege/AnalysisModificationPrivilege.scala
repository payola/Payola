package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.permission.action.AnalysisModificationAction

class AnalysisModificationPrivilege(a: Analysis) extends AnalysisPrivilege[AnalysisModificationAction](a)
{
}
