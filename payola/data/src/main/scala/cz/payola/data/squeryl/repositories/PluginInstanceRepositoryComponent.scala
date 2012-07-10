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
        override def persist(entity: AnyRef): PluginInstance = {
            // First persist ParameterInstance ...
            val persistedInstance = super.persist(entity)

            // ... then associate all parameter values ...
            persistedInstance.associateParameterValues()

            // ... and finally return persister ParameterInstance
            persistedInstance
        }
    }
}
