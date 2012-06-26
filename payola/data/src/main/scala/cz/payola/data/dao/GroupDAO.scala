package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.Group
import org.squeryl.PrimitiveTypeMode._

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{
    /**
      * Inserts or updated [[cz.payola.common.entities.Group]].
      *
      * @param g - gourp to insert or update
      * @return Returns persisted [[cz.payola.data.entities.Group]]
      */
    def persist(g: cz.payola.common.entities.Group): Group = {
        val group = Group(g)
        super.persist(group)
    }

    /**
      * Returns [[cz.payola.data.entities.Group]]s owned by specified owner. Result may be paginated
      *
      * @param ownerId - id of groups owner
      * @param pagination - Optionally specified pagination
      * @return Returns collecntion of [[cz.payola.data.entities.Group]]s
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
