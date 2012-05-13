package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.IntParameterValue

class IntParameterInstanceDAO extends EntityDAO[IntParameterValue](PayolaDB.intParameterValues)
{
}
