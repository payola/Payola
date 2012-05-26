package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.Group

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{
}
