package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.IntParameterDbRepresentation

class IntParameterDAO extends EntityDAO[IntParameterDbRepresentation](PayolaDB.intParameters)
{
}
