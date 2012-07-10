package cz.payola.model

import cz.payola.data._
import cz.payola.domain.Entity

trait EntityModelComponent
{
    self: DataContextComponent =>

    abstract class EntityModel[+A <: Entity](val repository: Repository[A])
    {
        def getById(id: String): Option[A] = repository.getById(id)

        def getAll: Seq[A] = repository.getAll()

        def persist(entity: Entity) {
            repository.persist(entity)
        }

        def remove(entity: Entity): Boolean = repository.removeById(entity.id)
    }
}
