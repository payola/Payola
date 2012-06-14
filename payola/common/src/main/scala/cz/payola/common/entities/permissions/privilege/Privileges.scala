package cz.payola.common.entities.permissions.privilege

import cz.payola.common.entities._

trait Privilege[U <: Entity] extends Entity
{
    protected val obj: U
}

trait GroupPrivilege extends Privilege[Group]

trait AnalysisPrivilege extends Privilege[Analysis]

trait AccessAnalysisResultOnlyPrivilege

trait AccessAnalysisDataPrivilege
