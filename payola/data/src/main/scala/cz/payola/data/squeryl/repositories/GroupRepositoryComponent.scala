package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.Group
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait GroupRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val groupRepository = new TableRepository[Group](schema.groups, Group) with GroupRepository[Group]
    {
        def getAllByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None) : Seq[Group] = {
            val query = from(table)(g =>
                where (g.ownerId.getOrElse("") === ownerId)
                select (g)
                orderBy (g.name)
            )

            evaluateCollectionResultQuery(query, pagination)
        }
    }
}
