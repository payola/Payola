package cz.payola.data.entities.dao

import cz.payola.data.entities.schema.PayolaDB
import cz.payola.data.entities.{User, Group}

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{

}
