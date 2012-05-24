package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.BooleanParameter

class BooleanParameterDAO extends EntityDAO[BooleanParameter](PayolaDB.booleanParameters)
{
}
