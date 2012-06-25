package cz.payola.domain.entities.privileges

import cz.payola.domain.entities._
import cz.payola.domain.entities.settings.ontology.Customization

// TODO

/**This class narrows down the privilege subject to some group.
  *
  * @param g A group which is the subject of this privilege.
  */
class GroupPrivilege(g: Group) extends Privilege[Group](g)


/** This class narrows down the privilege subject to some analysis.
  *
  * @param a An analysis which is the subject of this privilege.
  */
abstract class AnalysisPrivilege(a: Analysis) extends Privilege[Analysis](a) with PublicPrivilege


/** Grants the right to access the data of analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisDataPrivilege(a: Analysis) extends AnalysisPrivilege(a)


/** Allows the user to access result of the analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisResultOnlyPrivilege(a: Analysis) extends AnalysisPrivilege(a)


/**This class narrows down the privilege subject to some ontology customization.
  *
  * @param c An ontology customization which is the subject of this privilege.
  */
class OntologyCustomizationPrivilege(c: Customization) extends Privilege[Customization](c) with PublicPrivilege

