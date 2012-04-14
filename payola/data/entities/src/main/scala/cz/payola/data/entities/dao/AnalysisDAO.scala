package cz.payola.data.entities.dao

import cz.payola.data.entities.Analysis
import cz.payola.data.entities.schema.PayolaDB

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
}
