package cz.payola.data.entities

import org.squeryl.KeyedEntity
import cz.payola.domain.entities.ParameterInstance
import cz.payola.domain.entities.parameters.ParameterInstance
import collection.immutable

class PluginInstance(plugin: Plugin, parameterInstances: immutable.Seq[ParameterInstance[_]])
    extends cz.payola.domain.entities.PluginInstance(plugin, parameterInstances)
    with KeyedEntity[String]
    with PersistableEntity
