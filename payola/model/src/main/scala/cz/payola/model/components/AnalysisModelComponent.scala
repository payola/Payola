package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.privileges.AccessAnalysisPrivilege

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val analysisModel = new ShareableEntityModel[Analysis](analysisRepository, classOf[AccessAnalysisPrivilege])
    {
        def addBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int) = {
            val source = pluginInstanceRepository.getById(sourceId)
            val target = pluginInstanceRepository.getById(targetId)

            if (!source.isDefined || !target.isDefined){
                throw new Exception("Invalid source or target.")
            }

            getById(analysisId).map{a =>
                a.addBinding(source.get, target.get, inputIndex)
                persist(a)
            }.getOrElse{
                throw new Exception("Unknown analysis.")
            }
        }

        def create(owner: User): Analysis = {
            val instance = new Analysis("", Some(owner))
            analysisRepository.persist(instance)
            instance
        }
    }
}
