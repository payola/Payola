package cz.payola.domain.entities.generic

import java.util.UUID
import cz.payola.common.entities.Entity

class ConcreteEntity(val id: String = UUID.randomUUID.toString) extends Entity

