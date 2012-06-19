package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities._
import cz.payola.common.entities.settings.ontology.Customization

/** Privilege trait. Grants the user some rights around the object U.
  *
  * @tparam U Object of the privilege.
  */
trait Privilege[U <: Entity] extends Entity
{
    protected val obj: U
}

/** Group privilege. Denotes membership in the group. */
trait GroupPrivilege extends Privilege[Group]

/** Analysis privilege. The ResultOnly privilege grants permission to view the only
  *  the result of the analysis, whereas the Data privilege allows you view the whole
  *  analysis including potentially private data.
  */
trait AnalysisPrivilege extends Privilege[Analysis]
trait AccessAnalysisResultOnlyPrivilege extends AnalysisPrivilege
trait AccessAnalysisDataPrivilege extends AnalysisPrivilege

/** Grants access to other user's ontology customization. */
trait OntologyCustomizationPrivilege extends Privilege[Customization]
