package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.StringParameterValue

class StringParameterInstanceDAO extends EntityDAO[StringParameterValue](PayolaDB.stringParameterValues)
{
}
