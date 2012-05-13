package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.Plugin

class PluginDAO extends EntityDAO[Plugin](PayolaDB.plugins)
{
}
