package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.PluginInstanceBinding

class PluginInstanceBindingDAO extends EntityDAO[PluginInstanceBinding](PayolaDB.pluginInstanceBindings)
{
}
