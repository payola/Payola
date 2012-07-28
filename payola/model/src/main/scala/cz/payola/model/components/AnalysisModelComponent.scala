package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.model._
import cz.payola.domain.entities.privileges.AccessAnalysisPrivilege
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.parameters.StringParameterValue

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>

    lazy val analysisModel = new ShareableEntityModel(analysisRepository, classOf[Analysis])
    {
        def addBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int) {
            getById(analysisId).map {
                a =>
                    val source = a.pluginInstances.find(_.id == sourceId)
                    val target = a.pluginInstances.find(_.id == targetId)

                    if (!source.isDefined || !target.isDefined) {
                        throw new Exception("Invalid source or target.")
                    }

                    a.addBinding(source.get, target.get, inputIndex)
            }.getOrElse {
                throw new Exception("Unknown analysis.")
            }
        }

        def create(owner: User, name: String): Analysis = {
            val analysis = new Analysis(name, Some(owner))
            persist(analysis)
            analysis
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

        def cloneDataSource(dataSource: DataSource, analysisId: String): PluginInstance = {

            val analysis = analysisRepository.getById(analysisId).getOrElse{
                throw new Exception("Analysis not found.")
            }

            val paramValues = dataSource.parameterValues.map {
                value =>
                    value match {
                        case v:StringParameterValue => new StringParameterValue(v.parameter.asInstanceOf[StringParameter],v.value)
                        case v:FloatParameterValue => new FloatParameterValue(v.parameter.asInstanceOf[FloatParameter],v.value)
                        case v:IntParameterValue => new IntParameterValue(v.parameter.asInstanceOf[IntParameter],v.value)
                        case v:BooleanParameterValue => new BooleanParameterValue(v.parameter.asInstanceOf[BooleanParameter],v.value)
                        case _ => throw new Exception("Unsupported parameter value type.")
                    }
            }

            val clone = new PluginInstance(dataSource.plugin, paramValues)
            clone.isEditable_=(true)
            analysis.addPluginInstance(clone)

            clone
        }

        def setParameterValue(user: User, analysisId: String, pluginInstanceId: String, parameterName: String,
            value: String) {
            val analysis = user.ownedAnalyses
                .find(_.id == analysisId)
                .get

            val pluginInstance = analysis.pluginInstances.find(_.id == pluginInstanceId)

            pluginInstance.map {
                i =>

                    val option = i.getParameterValue(parameterName)

                    if (!option.isDefined) {
                        throw new Exception("Unknown parameter name: " + parameterName + ".")
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
