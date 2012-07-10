package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.domain.RdfStorageComponent
import scala.Some
import scala.Some
import cz.payola.data.PaginationInfo
import cz.payola.model.EntityModelComponent

trait GroupModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val groupModel = new EntityModel(groupRepository)
    {
        def create(name: String, owner: User): Group = {
            repository.persist(new Group(name, owner))
        }
    }
}
