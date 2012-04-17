package cz.payola.data.entities.dao

import cz.payola.data.entities.Analysis
import cz.payola.data.entities.schema.PayolaDB
import org.squeryl.PrimitiveTypeMode._

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
    private val EVERY_USER: String = "";

    def getTopAnalyses(count: Int = 10): collection.Seq[Analysis] = {
        getTopAnalysesByUser(EVERY_USER)
    }

    def getTopAnalysesByUser(userId: String, count: Int = 10): collection.Seq[Analysis] = {
        require(count >= 0, "Count must be >= 0")
        // Get by all users or just by specified one
        val query = table.where(a => userId === EVERY_USER or a.ownerId === userId)

        evaluateCollectionResultQuery(query, 0, count)
    }
}
