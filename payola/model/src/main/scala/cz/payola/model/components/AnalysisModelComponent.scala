package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val analysisModel = new EntityModel(analysisRepository)
    {
        def getTop: Seq[Analysis] = {
            // TODO repository.getTopAnalyses()
            Nil
        }

        def getPublicByOwner(owner: User) = {
            // TODO repository.getTopAnalysesByUser(owner.id)
            Nil
        }
    }
}
