package cz.payola.data.entities

import org.squeryl.KeyedEntity
<<<<<<< HEAD
import cz.payola.domain.entities.analyses.{ParameterValue, PluginInstance}
import cz.payola.domain.entities.ParameterInstance
import cz.payola.domain.entities.parameters.ParameterInstance
import collection.immutable

class PluginInstance(plugin: Plugin, parameterInstances: immutable.Seq[ParameterInstance[_]])
    extends cz.payola.domain.entities.AnalyticalPluginInstance(plugin, parameterInstances)
    with KeyedEntity[String]
    with PersistableEntity
=======
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
>>>>>>> develop
