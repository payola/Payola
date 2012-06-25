package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginDbRepresentation
import cz.payola.domain.entities.Plugin

class PluginDAO extends EntityDAO[PluginDbRepresentation](PayolaDB.plugins)
{
    def getByName(pluginName: String): Option[Plugin] = {
        // Get plugin represenatation from DB
        val pluginDb: Option[PluginDbRepresentation] =
            evaluateSingleResultQuery(table.where(p => p.name === pluginName))

        if (pluginDb.isDefined) {
            Some(pluginDb.get.createPlugin())
        }
        else {
            // Not found
            None
        }
    }

    def persist(p: Plugin): Option[PluginDbRepresentation] = {
        val pluginDb = PluginDbRepresentation(p)

        // First persist plugin ...
        val result = super.persist(pluginDb)

        // ... then assign parameters to plugin if is persisted
        if (result.isDefined) {
            p.parameters.map(pluginDb.addParameter(_))
        }

        result
    }
}
