package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.permissions.action.AccessAnalysisDataAction

class AccessAnalysisDataPrivilege(a: Analysis) extends AnalysisPrivilege[AccessAnalysisDataAction](a)
{
}
