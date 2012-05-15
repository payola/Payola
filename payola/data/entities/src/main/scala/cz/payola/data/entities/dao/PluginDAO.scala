package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.PluginDbRepresentation
import cz.payola.domain.entities.analyses.Plugin

class PluginDAO extends EntityDAO[PluginDbRepresentation](PayolaDB.plugins)
{
    def getByName(pluginName: String): Option[Plugin]  = {

        // Get plugin represenatation from DB
        val pluginDb: Option[PluginDbRepresentation] =
            evaluateSingleResultQuery(table.where(p => p.name === pluginName))

        val parameters = pluginDb.parameters
        val name = pluginDb.name
        val inputCount = pluginDb.inputCount
        val id = pluginDb.id
        val pluginClass = pluginDb.pluginClass

        // Return as domain.Plugin
        new Plugin(name, inputCount, parameters);
    }
}
