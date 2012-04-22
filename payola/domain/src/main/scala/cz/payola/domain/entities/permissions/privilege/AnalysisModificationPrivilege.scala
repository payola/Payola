package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis

/** Grants the user right to modify analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AnalysisModificationPrivilege(a: Analysis) extends AnalysisPrivilege(a)
{
}
