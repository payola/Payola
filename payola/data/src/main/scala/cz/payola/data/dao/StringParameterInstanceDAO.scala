package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.StringParameterValue

class StringParameterInstanceDAO extends EntityDAO[StringParameterValue](PayolaDB.stringParameterValues)
{
}
