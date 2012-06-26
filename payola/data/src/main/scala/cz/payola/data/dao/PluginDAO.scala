package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.PluginDbRepresentation
import cz.payola.domain.entities.Plugin

class PluginDAO extends EntityDAO[PluginDbRepresentation](PayolaDB.plugins)
{
    /**
      * Returns [[cz.payola.domain.entities.Plugin]] by its name.
      *
      * @param pluginName - name of a plugin to search
      * @return Return Some([[cz.payola.domain.entities.Plugin]]) if found, None otherwise
      */
    def getByName(pluginName: String): Option[Plugin] = {
        // Get plugin representation from DB
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

    /**
      * Inserts or updates [[cz.payola.domain.entities.Plugin]].
      *
      * @param p - plugin to insert or update
      * @return Returns persisted [[cz.payola.domain.entities.Plugin]]
      */
    def persist(p: Plugin): Plugin = {
        val pluginDb = PluginDbRepresentation(p)

        // First persist plugin ...
        val result = super.persist(pluginDb)

        // ... then assign parameters
        p.parameters.map(pluginDb.addParameter(_))

        result.createPlugin()
    }
}
