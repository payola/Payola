package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.common.entities.plugins
import cz.payola.data.squeryl._

trait PluginInstanceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceRepository = new TableRepository[PluginInstance](schema.pluginInstances, PluginInstance)
    {
        override def persist(entity: AnyRef): PluginInstance = {
            val persistedInstance = super.persist(entity)
            persistedInstance.associateParameterValues()
            persistedInstance
        }
    }
}
