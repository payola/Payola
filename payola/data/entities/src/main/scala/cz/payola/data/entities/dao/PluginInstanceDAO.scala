package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.PluginInstance

class PluginInstanceDAO extends  EntityDAO[PluginInstance](PayolaDB.pluginInstances)
{
}
