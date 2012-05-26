package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses.parameters.FloatParameter

class FloatParameterDAO extends EntityDAO[FloatParameter](PayolaDB.floatParameters)
{
}
