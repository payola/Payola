package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginInstanceBinding

class PluginInstanceBindingDAO extends EntityDAO[PluginInstanceBinding](PayolaDB.pluginInstanceBindings)
{
}
