package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.StringParameter

class StringParameterDAO extends EntityDAO[StringParameter](PayolaDB.stringParameters)
{
}
