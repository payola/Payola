package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.privileges.UsePluginPrivilege

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val pluginModel = new ShareableEntityModel[Plugin](pluginRepository, classOf[UsePluginPrivilege])
    {
        def getByName(name: String): Option[Plugin] = pluginRepository.getByName(name)
    }
}
