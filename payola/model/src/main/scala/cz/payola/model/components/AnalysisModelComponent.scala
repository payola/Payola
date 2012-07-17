package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.model._
import cz.payola.domain.entities.privileges.AccessAnalysisPrivilege
import cz.payola.domain.entities.plugins.parameters._

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>
    lazy val analysisModel = new ShareableEntityModel[Analysis](
        analysisRepository,
        classOf[AccessAnalysisPrivilege],
        (user: User) => user.ownedAnalyses)
    {
        def addBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int) {
            getById(analysisId).map{a =>
                val source = a.pluginInstances.find(_.id == sourceId)
                val target = a.pluginInstances.find(_.id == targetId)
    
                if (!source.isDefined || !target.isDefined){
                    throw new Exception("Invalid source or target.")
                }
                
                a.addBinding(source.get, target.get, inputIndex)
            }.getOrElse{
                throw new Exception("Unknown analysis.")
            }
        }

        def create(owner: User, name: String): Analysis = {
            val instance = new Analysis(name, Some(owner))
            analysisRepository.persist(instance)
            instance
        }

        def createPluginInstance(pluginId: String, analysisId: String): PluginInstance = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val instance = pluginRepository.getById(pluginId).map(_.createInstance()).getOrElse {
                throw new ModelException("Unknown plugin ID.")
            }

            analysis.addPluginInstance(instance)
            instance
        }

        def setParameterValue(user: User, analysisId: String, pluginInstanceId: String, parameterName: String,
            value: String) {

            val analysis = user.ownedAnalyses
                .find(_.id == analysisId)
                .get

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
        
        def removePluginInstanceById(analysisId: String, pluginInstanceId: String): Boolean = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val instance = analysis.pluginInstances.find(_.id == pluginInstanceId).getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }

            analysis.removePluginInstance(instance)
            analysis.pluginInstances.contains(instance)
        }

        def removePluginInstanceBindingById(analysisId: String, pluginInstanceBindingId: String): Boolean = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val binding = analysis.pluginInstanceBindings.find(_.id == pluginInstanceBindingId).getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }

            analysis.removeBinding(binding)
            analysis.pluginInstanceBindings.contains(binding)
        }
    }
}
