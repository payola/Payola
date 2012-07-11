package cz.payola.model

import cz.payola.data._
import cz.payola.domain.Entity
import cz.payola.domain.entities._
import cz.payola.model.components.GroupModelComponent

trait EntityModelComponent
{
    self: DataContextComponent  =>

    abstract class EntityModel[+A <: Entity](val repository: Repository[A])
    {
        def getById(id: String): Option[A] = repository.getById(id)

        def getAll: Seq[A] = repository.getAll()

        def persist(entity: Entity) {
            repository.persist(entity)
        }

        def remove(entity: Entity): Boolean = repository.removeById(entity.id)
    }

    class ShareableEntityModel[A <: ShareableEntity, B <: Privilege[A]](
        override val repository: ShareableEntityRepository[A],
        val accessPrivilegeClass: Class[_])
        extends EntityModel[A](repository)
    {
        def getAccessibleToUser(user: Option[User]): Seq[A] = {
            val granted = user.map { u =>
                val memberGroups = groupRepository.getAll().filter(_.hasMember(u))
                val granteeIds = u.id +: memberGroups.map(_.id)
                val privileges = privilegeRepository.getAllGrantedTo[B](granteeIds, accessPrivilegeClass)
                privileges.map(_.obj)
            }
            repository.getAllPublic +: granted.getOrElse(Nil)
        }

        def getAccessibleToUserByOwner(user: Option[User], owner: User): Seq[A] = {
            // TODO
            analysisRepository.getAllPublic
        }

        def getAllAccessible(user: Option[User]): Seq[A] = {
            val accessible =

            public // ++ accessible
        }
    }
}
