package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.IntParameter

class IntParameterDAO extends EntityDAO[IntParameter](PayolaDB.intParameters)
{
}
