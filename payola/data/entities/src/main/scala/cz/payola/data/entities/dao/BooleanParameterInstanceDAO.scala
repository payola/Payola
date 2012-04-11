package cz.payola.data.entities.dao

import cz.payola.data.entities.BooleanParameterInstance
import cz.payola.data.entities.schema.PayolaDB

class BooleanParameterInstanceDAO extends EntityDAO[BooleanParameterInstance](PayolaDB.booleanParameterInstances)
{
}
