package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent

trait GroupModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val groupModel = new EntityModel(groupRepository)
    {
        def create(name: String, owner: User): Group = {
            repository.persist(new Group(name, owner))
        }

        def findAvailableMembers(group: Group, owner: User, term: String) : Seq[User] = {
            userRepository.getAllWithNameLike(term).diff(owner +: group.members)
        }
    }
}
