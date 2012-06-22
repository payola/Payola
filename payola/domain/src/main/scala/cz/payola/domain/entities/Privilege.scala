package cz.payola.domain.entities

abstract class Privilege[A <: Entity](val obj: A) extends Entity with cz.payola.common.entities.Privilege[A]
