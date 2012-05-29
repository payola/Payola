package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.Group

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{
    def persist(g: cz.payola.common.entities.Group): Option[Group] = {
        val group = Group(g)
        super.persist(group)
    }
}
