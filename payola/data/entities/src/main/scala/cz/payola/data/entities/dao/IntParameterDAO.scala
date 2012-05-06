package cz.payola.data.entities.dao

import cz.payola.data.entities.IntParameter
import cz.payola.data.entities.schema.PayolaDB

class IntParameterDAO extends EntityDAO[IntParameter](PayolaDB.intParameters)
{
}
