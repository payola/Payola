package cz.payola.data.entities

import org.squeryl.KeyedEntity
import cz.payola.domain.entities.analyses.Plugin

class Plugin(name: String)
    extends cz.payola.domain.entities.AnalyticalPlugin(name, Nil) with KeyedEntity[String] with PersistableEntity
