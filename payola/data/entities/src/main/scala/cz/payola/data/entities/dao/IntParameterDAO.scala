package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.IntParameter

class IntParameterDAO extends EntityDAO[IntParameter](PayolaDB.intParameters)
{
}
