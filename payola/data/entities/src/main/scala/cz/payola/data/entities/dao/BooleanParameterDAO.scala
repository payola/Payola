package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.BooleanParameterDbRepresentation

class BooleanParameterDAO extends EntityDAO[BooleanParameterDbRepresentation](PayolaDB.booleanParameters)
{
}
