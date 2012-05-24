package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginInstance

class PluginInstanceDAO extends EntityDAO[PluginInstance](PayolaDB.pluginInstances)
{
}
