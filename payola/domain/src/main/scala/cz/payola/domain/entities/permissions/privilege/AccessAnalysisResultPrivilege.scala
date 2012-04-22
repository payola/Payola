package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis

/** Allows the user to access result of the analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisResultPrivilege(a: Analysis) extends AnalysisPrivilege(a)
{
}
