package cz.payola.common.entities.permissions.action

import cz.payola.common.entities.Entity

trait Action[EntityType]{
    type EntityType <: Entity

    //val subject: EntityType
}
