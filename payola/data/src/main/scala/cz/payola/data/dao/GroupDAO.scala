package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.Group
import org.squeryl.PrimitiveTypeMode._

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{
    def persist(g: cz.payola.common.entities.Group): Option[Group] = {
        val group = Group(g)
        super.persist(group)
    }

    def getByOwnerId(id: String, offset: Int = 0, count: Int = 0) : Seq[Group] = {
        /* TODO: fails on login
        val query = from(table)(g =>
            where (g.owner.id === id)
            select (g)
            orderBy (g.name)
        )

        evaluateCollectionResultQuery(query, offset, count)
        */
        List()
    }
}
