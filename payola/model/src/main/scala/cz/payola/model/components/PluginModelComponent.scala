package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>
    lazy val pluginModel = new EntityModel(pluginRepository)
    {
        def getByName(name: String): Option[Plugin] = pluginRepository.getByName(name)
    }
}
