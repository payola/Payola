package cz.payola.domain.entities

import cz.payola.domain._

abstract class Privilege[A <: Entity](
    val obj: A,
    protected var _id: String = IDGenerator.newId)
    extends Entity(_id) with cz.payola.common.entities.Privilege[A]
