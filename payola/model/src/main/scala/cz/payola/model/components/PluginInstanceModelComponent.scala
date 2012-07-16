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
    }
}
