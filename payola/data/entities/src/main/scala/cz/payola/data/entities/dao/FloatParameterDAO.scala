package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.FloatParameterDbRepresentation

class FloatParameterDAO extends EntityDAO[FloatParameterDbRepresentation](PayolaDB.floatParameters)
{
}
