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

    class ShareableEntityModel[A <: ShareableEntity with OptionallyOwnedEntity](
        override val repository: ShareableEntityRepository[A],
        accessPrivilegeClass: Class[_])
        extends EntityModel[A](repository)
    {
        def getAccessibleToUser(user: Option[User]): Seq[A] = {
            repository.getAllPublic ++ getGrantedToUser(user, groupRepository.getAll())
        }

        def getAccessibleToUserByOwner(user: Option[User], owner: User): Seq[A] = {
            repository.getAllPublicByOwnerId(Some(owner.id)) ++ getGrantedToUser(user, owner.ownedGroups)
        }

        private def getGrantedToUser(user: Option[User], groups: Seq[Group]): Seq[A] = {
            user.map { u =>
                val granteeIds = u.id +: groups.filter(_.hasMember(u)).map(_.id)
                val privileges = privilegeRepository.getAllGrantedTo(granteeIds, accessPrivilegeClass)
                privileges.map(_.obj.asInstanceOf[A])
            }.getOrElse(Nil)
        }
    }
}
