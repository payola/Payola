package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.FloatParameter

class FloatParameterDAO extends EntityDAO[FloatParameter](PayolaDB.floatParameters)
{
}
