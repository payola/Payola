package cz.payola.domain.entities

import cz.payola.domain.IDGenerator

class Entity(val id: String = IDGenerator.newId) extends cz.payola.common.entities.Entity
