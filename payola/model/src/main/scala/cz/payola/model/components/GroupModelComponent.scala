package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.domain.RdfStorageComponent
import scala.Some
import scala.Some
import cz.payola.data.PaginationInfo
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.plugins.DataSource

trait GroupModelComponent extends EntityModelComponent
{self: DataContextComponent =>
    lazy val groupModel = new EntityModel(groupRepository)
    {
        def create : Group = {
            //TODO
            getById("").get
        }

        def create(name: String, owner: User): Group = {
            repository.persist(new Group(name, owner))
        }

        def getByOwner(user: Option[User], maxCount: Int = 10): Seq[Group] = {
            // TODO user.map(u => groupRepository.getByOwnerId(u.id, Some(PaginationInfo(0, maxCount)))).getOrElse(Nil)
            Nil
        }

        def getByOwnerAndId(owner: User, id: String): Option[Group] = {
            repository.getById(id).flatMap(g => if (g.owner == owner) Some(g) else None)
        }
    }
}
