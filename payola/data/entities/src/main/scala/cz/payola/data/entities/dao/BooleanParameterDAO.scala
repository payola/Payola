package cz.payola.data.entities.dao

import cz.payola.data.entities.BooleanParameter
import cz.payola.data.entities.schema.PayolaDB

class BooleanParameterDAO extends EntityDAO[BooleanParameter](PayolaDB.booleanParameters)
{
}
