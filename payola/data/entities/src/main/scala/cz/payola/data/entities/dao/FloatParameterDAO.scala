package cz.payola.data.entities.dao

import cz.payola.data.entities.FloatParameter
import cz.payola.data.entities.schema.PayolaDB

class FloatParameterDAO extends EntityDAO[FloatParameter](PayolaDB.floatParameters)
{
}
