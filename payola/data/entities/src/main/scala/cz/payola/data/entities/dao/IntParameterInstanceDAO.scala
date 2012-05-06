package cz.payola.data.entities.dao

import cz.payola.data.entities.IntParameterInstance
import cz.payola.data.entities.schema.PayolaDB

class IntParameterInstanceDAO extends EntityDAO[IntParameterInstance](PayolaDB.intParameterInstances)
{
}
