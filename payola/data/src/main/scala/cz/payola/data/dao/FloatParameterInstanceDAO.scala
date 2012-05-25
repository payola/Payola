package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.FloatParameterValue

class FloatParameterInstanceDAO extends EntityDAO[FloatParameterValue](PayolaDB.floatParameterValues)
{
}
