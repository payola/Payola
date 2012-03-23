package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.entities.permissions.action.AccessAnalysisResultAction

class AccessAnalysisResultPrivilege(a: Analysis) extends AnalysisPrivilege[AccessAnalysisResultAction](a)
{
}
