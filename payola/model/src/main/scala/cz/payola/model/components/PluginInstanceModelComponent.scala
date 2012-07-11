package cz.payola.model.components

import cz.payola.data._
import cz.payola.model._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.parameters._

trait PluginInstanceModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PluginModelComponent =>
    lazy val pluginInstanceModel = new EntityModel(pluginInstanceRepository)
    {
        def create(pluginId: String, analysisId: String): PluginInstance = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val instance = pluginRepository.getById(pluginId).map(_.createInstance()).getOrElse {
                throw new ModelException("Unknown plugin ID.")
            }

            analysis.addPluginInstance(instance)
            instance
        }

        def setParameterValue(pluginInstanceId: String, parameterName: String, value: String) {
            getById(pluginInstanceId).map { i =>

                val parameterValue = i.getParameterValue(parameterName).orElse{
                    throw new Exception("Unknown parameter name: "+parameterName+".")
                }

                parameterValue match {
                    case v: BooleanParameterValue => v.value = value.toBoolean
                    case v: FloatParameterValue => v.value = value.toFloat
                    case v: IntParameterValue => v.value = value.toInt
                    case v: StringParameterValue => v.value = value
                    case _ => throw new Exception("Unknown parameter type.")
                }

                pluginInstanceRepository.persist(i)
            }.getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }
        }
    }
}
