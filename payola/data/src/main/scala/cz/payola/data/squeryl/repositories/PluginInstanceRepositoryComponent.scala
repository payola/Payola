package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.common.entities.plugins
import cz.payola.data.squeryl._

trait PluginInstanceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceRepository = new LazyTableRepository[PluginInstance](schema.pluginInstances, PluginInstance)
    {
        override def persist(entity: AnyRef): PluginInstance = wrapInTransaction {
            // First persist ParameterInstance then associate all parameter values
            val persistedInstance = super.persist(entity)

            persistedInstance.associateParameterValues()

            persistedInstance
        }
    }
}
