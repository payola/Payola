package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginInstance
import cz.payola.common.entities.plugins

class PluginInstanceDAO extends EntityDAO[PluginInstance](PayolaDB.pluginInstances)
{
    def persist(i: plugins.PluginInstance): Option[PluginInstance] = {
        val instance = PluginInstance(i)

        // First persist plugin instance ...
        val result = super.persist(instance)

        // ... then persist parameter values
        if (result.isDefined) {
            instance.associateParameterValues()
        }

        result
    }
}
