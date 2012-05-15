package cz.payola.data.entities.dao

import cz.payola.data.entities.PayolaDB
import cz.payola.data.entities.analyses.parameters.StringParameterDbRepresentation

class StringParameterDAO extends EntityDAO[StringParameterDbRepresentation](PayolaDB.stringParameters)
{
}
