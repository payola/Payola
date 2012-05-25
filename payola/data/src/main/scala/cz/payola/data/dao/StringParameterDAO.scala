package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.StringParameter

class StringParameterDAO extends EntityDAO[StringParameter](PayolaDB.stringParameters)
{
}
