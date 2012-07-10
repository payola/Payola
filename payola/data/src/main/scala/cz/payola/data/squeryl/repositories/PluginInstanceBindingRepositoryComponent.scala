package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding
import cz.payola.data.squeryl._

trait PluginInstanceBindingRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceBindingRepository = new LazyTableRepository[PluginInstanceBinding](
        schema.pluginInstanceBindings, PluginInstanceBinding)
}
