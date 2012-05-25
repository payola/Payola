package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis

/** Grants the right to access the data of analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisDataPrivilege(a: Analysis) extends AnalysisPrivilege(a)
