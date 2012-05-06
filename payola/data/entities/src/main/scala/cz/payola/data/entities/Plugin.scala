package cz.payola.data.entities

import org.squeryl.KeyedEntity
<<<<<<< HEAD
import cz.payola.domain.entities.analyses.Plugin

class Plugin(name: String)
    extends cz.payola.domain.entities.AnalyticalPlugin(name, Nil) with KeyedEntity[String] with PersistableEntity
=======
import schema.PayolaDB
import collection.mutable.ArrayBuffer
import cz.payola.domain.entities.Analysis._
import org.squeryl.PrimitiveTypeMode._

class Plugin(
        id: String,
        name: String)
    extends cz.payola.domain.entities.Plugin(id, name)
    with PersistableEntity
{
    private lazy val _pluginInstancesQuery =  PayolaDB.pluginsPluginInstances.left(this)

    def pluginInstances : collection.Seq[PluginInstance] = {
        evaluateCollection(_pluginInstancesQuery)
    }
}
>>>>>>> develop
