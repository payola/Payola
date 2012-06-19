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

    //TODO: OH - implement this
    def getByOwnerId(id: String, maxCount: Int) : Seq[Group] = {
        /* TODO: fails on login
        TODO: maxCount
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
