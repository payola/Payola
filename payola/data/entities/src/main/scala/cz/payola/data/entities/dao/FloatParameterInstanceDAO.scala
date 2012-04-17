package cz.payola.data.entities.dao

import cz.payola.data.entities.FloatParameterInstance
import cz.payola.data.entities.schema.PayolaDB

class FloatParameterInstanceDAO extends EntityDAO[FloatParameterInstance](PayolaDB.floatParameterInstances)
{
}
