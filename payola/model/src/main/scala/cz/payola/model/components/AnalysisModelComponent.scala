package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.privileges.AccessAnalysisPrivilege

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val analysisModel = new ShareableEntityModel[Analysis, AccessAnalysisPrivilege](analysisRepository,
        classOf[AccessAnalysisPrivilege])
    {
        def create(owner: User): Analysis = {
            val instance = new Analysis("", Some(owner))
            analysisRepository.persist(instance)
            instance
        }
    }
}
