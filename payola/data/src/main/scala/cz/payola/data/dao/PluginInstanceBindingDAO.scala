package cz.payola.data.dao

import cz.payola.data._
import cz.payola.data.entities.analyses.PluginInstanceBinding

trait PluginInstanceBindingDAOComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceBindingDAO = new PluginInstanceBindingDAO

    class PluginInstanceBindingDAO extends EntityDAO[PluginInstanceBinding](schema.pluginInstanceBindings)
        with DAO[PluginInstanceBinding]
{
    /**
      * Inserts or updates [[cz.payola.common.entities.analyses.PluginInstanceBinding]].
      *
      * @param b - binding to insert or update
      * @return Returns persisted [[cz.payola.data.entities.analyses.PluginInstanceBinding]]
      */
    def persist(b: cz.payola.common.entities.analyses.PluginInstanceBinding): PluginInstanceBinding = {
        super.persist(PluginInstanceBinding(b))
    }
}
}
