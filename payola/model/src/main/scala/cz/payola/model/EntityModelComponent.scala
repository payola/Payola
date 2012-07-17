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

    class ShareableEntityModel[A <: ShareableEntity with OptionallyOwnedEntity with NamedEntity](
        override val repository: ShareableEntityRepository[A],
        accessPrivilegeClass: Class[_],
        val ownedEntitiesGetter: User => Seq[A])
        extends EntityModel[A](repository)
    {
        def getAccessibleToUser(user: Option[User]): Seq[A] = {
            val public = repository.getAllPublic
            val owned = getOwnedByUser(user)
            val granted = getGrantedToUser(user, groupRepository.getAll())
            (public ++ owned ++ granted).distinct.sortBy(_.name)
        }

        def getAccessibleToUserById(user: Option[User], id: String): Option[A] = {
            getAccessibleToUser(user).find(_.id == id)
        }

        def getAccessibleToUserByOwner(user: Option[User], owner: User): Seq[A] = {
            val publicEntities = repository.getAllPublicByOwnerId(Some(owner.id))
            val entitiesOfOwner = if (user == owner) {
                getOwnedByUser(Some(owner))
            } else {
                getGrantedToUser(user, owner.ownedGroups)
            }
            (publicEntities ++ entitiesOfOwner).distinct.sortBy(_.name)
        }

        private def getGrantedToUser(user: Option[User], groups: Seq[Group]): Seq[A] = {
            user.map { u =>
                val granteeIds = u.id +: groups.filter(_.hasMember(u)).map(_.id)
                val privileges = privilegeRepository.getAllGrantedTo(granteeIds, accessPrivilegeClass)
                privileges.map(_.obj.asInstanceOf[A])
            }.getOrElse(Nil)
        }

        private def getOwnedByUser(user: Option[User]): Seq[A] = {
            user.map(ownedEntitiesGetter(_)).getOrElse(Nil)
        }
    }
}
