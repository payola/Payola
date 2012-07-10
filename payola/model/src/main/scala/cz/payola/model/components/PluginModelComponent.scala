package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.plugins.DataSource

trait PluginModelComponent extends EntityModelComponent
{self: DataContextComponent =>
    lazy val pluginModel = new EntityModel(pluginRepository)
    {
        def create : Plugin = {
            //TODO
            getById("").get
        }
    }
}
