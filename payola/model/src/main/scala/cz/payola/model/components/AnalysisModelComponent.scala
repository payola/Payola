package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val analysisModel = new EntityModel(analysisRepository)
    {
        def create(owner: User): Analysis = {
            val instance = new Analysis("", Some(owner))
            analysisRepository.persist(instance)
            instance
        }

        def getAccessibleToUser(user: Option[User]): Seq[Analysis] = {
            // TODO
            analysisRepository.getAllPublic
        }

        def getAccessibleToUserByOwner(user: Option[User], owner: User): Seq[Analysis] = {
            // TODO
            analysisRepository.getAllPublic
        }
    }
}
