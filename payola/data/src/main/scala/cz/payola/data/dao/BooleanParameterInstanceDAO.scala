package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.BooleanParameterValue

class BooleanParameterInstanceDAO extends EntityDAO[BooleanParameterValue](PayolaDB.booleanParameterValues)
{
}
