package cz.payola.data.entities.dao

import cz.payola.data.entities.StringParameter
import cz.payola.data.entities.schema.PayolaDB

class StringParameterDAO extends EntityDAO[StringParameter](PayolaDB.stringParameters)
{
}
