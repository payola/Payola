package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model._
import cz.payola.domain.entities.privileges.AccessAnalysisPrivilege
import cz.payola.domain.entities.plugins.parameters._
import scala.Some
import scala.Some
import scala.Some
import scala.Some

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>
    lazy val analysisModel = new ShareableEntityModel[Analysis](
        analysisRepository,
        classOf[AccessAnalysisPrivilege],
        (user: User) => user.ownedAnalyses)
    {
        def addBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int) = {
            val source = pluginInstanceRepository.getById(sourceId)
            val target = pluginInstanceRepository.getById(targetId)

            if (!source.isDefined || !target.isDefined) {
                throw new Exception("Invalid source or target.")
            }

            getById(analysisId).map { a =>
                a.addBinding(source.get, target.get, inputIndex)
                persist(a)
            }.getOrElse {
                throw new Exception("Unknown analysis.")
            }
        }

        def create(owner: User, name: String): Analysis = {
            val instance = new Analysis(name, Some(owner))
            analysisRepository.persist(instance)
            instance
        }

        def setParameterValue(user: User, analysisId: String, pluginInstanceId: String, parameterName: String,
            value: String) {

            val analysis = user.ownedAnalyses
                .find(_.id == analysisId)
                .getOrElse{throw new Exception("Analysis not found.")}

            val pluginInstance = analysis.pluginInstances.find(_.id == pluginInstanceId)

            pluginInstance.map { i =>

                val option = i.getParameterValue(parameterName)

                if (!option.isDefined){
                    throw new Exception("Unknown parameter name: "+parameterName+".")
                }

                val parameterValue = option.get

                parameterValue match {
                    case v: BooleanParameterValue => v.value = value.toBoolean
                    case v: FloatParameterValue => v.value = value.toFloat
                    case v: IntParameterValue => v.value = value.toInt
                    case v: StringParameterValue => v.value = value
                    case _ => throw new Exception("Unknown parameter type.")
                }

                analysisRepository.persistParameterValue(parameterValue)
            }.getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }
        }
    }
}
