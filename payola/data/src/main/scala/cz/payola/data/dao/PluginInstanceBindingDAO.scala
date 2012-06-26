package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginInstanceBinding

class PluginInstanceBindingDAO extends EntityDAO[PluginInstanceBinding](PayolaDB.pluginInstanceBindings)
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
