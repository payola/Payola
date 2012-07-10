package cz.payola.model

import cz.payola.data._
import cz.payola.domain.Entity
import cz.payola.domain.entities._
import cz.payola.model.components.GroupModelComponent

trait EntityModelComponent
{
    self: DataContextComponent  =>

    class EntityModel[+A <: Entity](val repository: Repository[A])
    {
        def getById(id: String): Option[A] = repository.getById(id)

        def getAll: Seq[A] = repository.getAll()

        def persist(entity: Entity) {
            repository.persist(entity)
        }

        def remove(entity: Entity): Boolean = repository.removeById(entity.id)
    }

    class ShareableEntityModel[A <: ShareableEntity](override val repository: ShareableEntityRepository[A])
        extends EntityModel[A](repository)
    {
        def getAllAccessible(user: Option[User]): Seq[A] = {
            val accessible = user.map { u =>
                val memberGroups = groupRepository.getAll.filter(_.hasMember(u))
                val granteeIds = u.id +: memberGroups.map(_.id)
                privilegeRepository.
            }
            val public = repository.getAllPublic
        }

        def getAllPublic: Seq[A] = {
            repository.get
        }


    }
}
