package cz.payola.data.entities.dao

import cz.payola.data.entities.StringParameterInstance
import cz.payola.data.entities.schema.PayolaDB

class StringParameterInstanceDAO extends EntityDAO[StringParameterInstance](PayolaDB.stringParameterInstances)
{
}
