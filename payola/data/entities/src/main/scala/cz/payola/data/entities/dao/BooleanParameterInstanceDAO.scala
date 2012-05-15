package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.BooleanParameterValue

class BooleanParameterInstanceDAO extends EntityDAO[BooleanParameterValue](PayolaDB.booleanParameterValues)
{
}
