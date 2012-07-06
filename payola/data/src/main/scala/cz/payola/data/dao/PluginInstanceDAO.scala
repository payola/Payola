package cz.payola.data.dao

import cz.payola.data._
import cz.payola.data.entities.plugins.PluginInstance
import cz.payola.common.entities.plugins

trait PluginInstanceDAOComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceDAO = new PluginInstanceDAO

    class PluginInstanceDAO extends EntityDAO[PluginInstance](schema.pluginInstances) with DAO[PluginInstance]
{
    /**
      * Inserts or updates [[cz.payola.common.entities.analyses.PluginInstance]].
      *
      * @param i - plugin instance to persist
      * @return Returns persisted [[cz.payola.data.entities.analyses.PluginInstanceBinding]]
      */
    def persist(i: plugins.PluginInstance): PluginInstance = {
        val instance = PluginInstance(i)

        // First persist plugin instance ...
        val result = super.persist(instance)

        // ... then persist parameter values
        instance.associateParameterValues()

        result
    }
}
}
