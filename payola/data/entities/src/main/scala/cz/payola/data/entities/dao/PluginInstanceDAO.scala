package cz.payola.data.entities.dao

import cz.payola.data.entities.PluginInstance
import cz.payola.data.entities.schema.PayolaDB

class PluginInstanceDAO extends  EntityDAO[PluginInstance](PayolaDB.pluginInstances)
{
}
