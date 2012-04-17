package cz.payola.data.entities

import org.squeryl.KeyedEntity
import schema.PayolaDB
import collection.mutable.ArrayBuffer
import org.squeryl.PrimitiveTypeMode._

class PluginInstance(
        id: String,
        plugin: Plugin)
    extends cz.payola.domain.entities.PluginInstance(id, plugin)
    with PersistableEntity
{
    val pluginId: String = if (plugin == null) "" else plugin.id

    private lazy val _analysesQuery =  PayolaDB.analysesPluginInstances.right(this)

    def analyses : collection.Seq[Analysis] = {
        evaluateCollection(_analysesQuery)
    }
}
