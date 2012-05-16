package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.BooleanParameter

class BooleanParameterDAO extends EntityDAO[BooleanParameter](PayolaDB.booleanParameters)
{
}
