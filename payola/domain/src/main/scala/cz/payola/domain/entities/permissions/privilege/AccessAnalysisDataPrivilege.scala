package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.permission.action.AccessAnalysisDataAction

class AccessAnalysisDataPrivilege(a: Analysis) extends AnalysisPrivilege[AccessAnalysisDataAction](a)
{
}
