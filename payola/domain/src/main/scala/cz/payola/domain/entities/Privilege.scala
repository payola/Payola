package cz.payola.domain.entities

import cz.payola.domain.Entity

abstract class Privilege[A <: Entity](val obj: A) extends Entity with cz.payola.common.entities.Privilege[A]
