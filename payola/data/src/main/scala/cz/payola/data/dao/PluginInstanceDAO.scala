package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.plugins.PluginInstance
import cz.payola.common.entities.plugins

class PluginInstanceDAO extends EntityDAO[PluginInstance](PayolaDB.pluginInstances)
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
