package cz.payola.domain.permission.privilege

import cz.payola.domain.entities.Analysis
import cz.payola.domain.permission.action.AccessAnalysisResultAction

class AccessAnalysisResultPrivilege(a: Analysis) extends AnalysisPrivilege[AccessAnalysisResultAction](a)
{
}
