package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.IntParameterValue

class IntParameterInstanceDAO extends EntityDAO[IntParameterValue](PayolaDB.intParameterValues)
{
}
