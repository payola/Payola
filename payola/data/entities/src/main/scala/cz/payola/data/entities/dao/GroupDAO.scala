package cz.payola.data.entities.dao

import cz.payola.data.entities.{Group, PayolaDB}

class GroupDAO extends EntityDAO[Group](PayolaDB.groups)
{

}
