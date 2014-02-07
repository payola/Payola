package cz.payola.model

import cz.payola.common.ValidationException
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.Entity
import cz.payola.domain.entities._
import cz.payola.data._
import cz.payola.data.PaginationInfo
import cz.payola.model.components.PrivilegeModelComponent

trait EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>

    abstract class EntityModel[+A <: Entity](val repository: Repository[A])
    {
        def getById(id: String): Option[A] = repository.getById(id)

        def getByIds(ids: Seq[String]): Seq[A] = repository.getByIds(ids)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A] = repository.getAll(pagination)

        def persist(entity: Entity) {
            simplePersist(entity)
        }

        def remove(entity: Entity): Boolean = repository.removeById(entity.id)

        protected def simplePersist(entity: Entity) {
            repository.persist(entity)
        }

        protected def uniqueKeyCheckedPersist(entity: Entity, uniqueFieldName: String) {
            try {
                simplePersist(entity)
            } catch {
                case d: DataException if d.isUniqueKeyViolation => {
                    throw new ValidationException(uniqueFieldName,
                        "There already exists a %s with this %s.".format(entity.classNameText, uniqueFieldName))
                }
            }
        }
    }

    class ShareableEntityModel[A <: ShareableEntity with OptionallyOwnedEntity with NamedEntity](
        override val repository: ShareableEntityRepository[A],
        val entityClass: Class[_])
        extends EntityModel[A](repository)
    {
        override def persist(entity: Entity) {
            uniqueKeyCheckedPersist(entity, "name")
        }

        def getAccessibleToUser(user: Option[User], forListing : Boolean = false): Seq[A] = {
            val public = repository.getAllPublic(forListing)
            val owned = getOwnedByUser(user)
            val granted = getGrantedToUser(user, groupRepository.getAll())

            (public ++ owned ++ granted).foldLeft(Nil: List[A]) {(acc, next) =>
                if (acc.exists(_.id == next.id)) acc else next :: acc }.reverse.sortBy(_.name)
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

        def getByName(name: String): Option[A] = {
            repository.getByName(name)
        }

        private def getGrantedToUser(user: Option[User], groups: Seq[Group]): Seq[A] = {
            user.map { u =>
                val granteeIds = u.id +: groups.filter(_.hasMember(u)).map(_.id)
                val privilegeClass = privilegeModel.getSharingPrivilegeClass(entityClass)
                val privileges = privilegeRepository.getAllByGranteeIds(granteeIds, privilegeClass)
                privileges.map(_.obj.asInstanceOf[A])
            }.getOrElse(Nil)
        }

        private def getOwnedByUser(user: Option[User]): Seq[A] = {
            user.map(_.getOwnedEntities(entityClass).map(_.asInstanceOf[A])).getOrElse(Nil)
        }
    }
}
