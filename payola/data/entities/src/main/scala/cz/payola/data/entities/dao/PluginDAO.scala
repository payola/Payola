package cz.payola.data.entities.dao

import cz.payola.data.entities.Plugin
import cz.payola.data.entities.schema.PayolaDB

class PluginDAO extends EntityDAO[Plugin](PayolaDB.plugins)
{
}
