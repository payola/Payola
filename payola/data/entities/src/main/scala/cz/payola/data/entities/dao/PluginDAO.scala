package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.Plugin

class PluginDAO extends EntityDAO[Plugin](PayolaDB.plugins)
{
    def getByName(pluginName: String): Option[Plugin] = {
        evaluateSingleResultQuery(table.where(p => p.name === pluginName))
    }
}
