package cz.payola.data.entities

import org.squeryl.KeyedEntity

class Plugin(name: String)
    extends cz.payola.domain.entities.Plugin(name, Nil) with KeyedEntity[String] with PersistableEntity
