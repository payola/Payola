package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.permissions.action.AnalysisModificationAction

class AnalysisModificationPrivilege(a: Analysis) extends AnalysisPrivilege[AnalysisModificationAction](a)
{
}
