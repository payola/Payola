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

        if (pluginDb.isDefined) {
            val parameters = pluginDb.get.parameters
            val name = pluginDb.get.name
            val inputCount = pluginDb.get.inputCount
            val id = pluginDb.get.id
            val className = pluginDb.get.pluginClass

            // Return as domain.Plugin
            instantiate(className, name, new java.lang.Integer(inputCount), parameters, id)
        }
        else {
            // Not found
            None
        }
    }

    def persist(plugin: Plugin) {
        val pluginClass = plugin.getClass.toString.replace("class ", "")
        val pluginDb = new PluginDbRepresentation(
            plugin.id,
            plugin.name,
            pluginClass,
            plugin.inputCount
        )

        // First persist plugin ...
        super.persist(pluginDb)

        // ... then assign parameters to plugin
        plugin.parameters.map(pluginDb.addParameter(_))
    }

    private def instantiate(className: String, args: AnyRef*): Option[Plugin] = {
        val pluginClass = java.lang.Class.forName(className)
        val constructor = pluginClass.getConstructors()(0)

        Some(constructor.newInstance(args:_*).asInstanceOf[Plugin])
    }
}
