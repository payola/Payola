package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.Group
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait GroupRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val groupRepository = new TableRepository[Group](schema.groups, Group)
    {
        /**
          * Returns [[cz.payola.data.squeryl.entities.Group]]s owned by specified owner. Result may be paginated
          *
          * @param ownerId - id of groups owner
          * @param pagination - Optionally specified pagination
          * @return Returns collecntion of [[cz.payola.data.squeryl.entities.Group]]s
          */
        def getByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None) : Seq[Group] = {
            val query = from(table)(g =>
                where (g.ownerId.getOrElse("") === ownerId)
                select (g)
                orderBy (g.name)
            )

            evaluateCollectionResultQuery(query, pagination)
        }
    }
}
