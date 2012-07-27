package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.Entity

trait GroupModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>

    lazy val groupModel = new EntityModel(groupRepository)
    {
        override def persist(entity: Entity) {
            uniqueKeyCheckedPersist(entity, "name")
        }

        def create(name: String, owner: User): Group = {
            val group = new Group(name, owner)
            persist(group)
            group
        }

        def findAvailableMembers(group: Group, owner: User, term: String) : Seq[User] = {
            userRepository.getAllWithNameLike(term).diff(owner +: group.members).sortBy(_.name)
        }
    }
}
