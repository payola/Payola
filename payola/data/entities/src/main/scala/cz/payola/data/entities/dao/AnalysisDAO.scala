package cz.payola.data.entities.dao

import cz.payola.data.entities.Analysis
import cz.payola.data.entities.User
import cz.payola.data.entities.schema.PayolaDB
import org.squeryl.PrimitiveTypeMode._

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
    def getPublicAnalysesByOwner(o: User, page: Int = 1, pageLength: Int = 0) = {
        val query = table.where(a => a.ownerId === o.id)

        transaction {
            query.page(page, pageLength).toSeq
        }
    }
}
