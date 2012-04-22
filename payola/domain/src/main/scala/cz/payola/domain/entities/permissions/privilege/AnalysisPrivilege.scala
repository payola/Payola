package cz.payola.domain.entities.permissions.privilege

import cz.payola.domain.entities.Analysis

/** This class narrows down the privilege subject to some analysis.
  *
  * @param a An analysis which is the subject of this privilege.
  */
abstract class AnalysisPrivilege(a: Analysis) extends Privilege[Analysis](a)
{
}
