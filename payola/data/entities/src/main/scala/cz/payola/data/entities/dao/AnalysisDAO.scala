package cz.payola.data.entities.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.entities.{PayolaDB, Analysis, User}
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query.{Selection, Projection, Typed}
import cz.payola.domain.entities.analyses.plugins.Join

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
    private val EVERY_USER: String = "00000000-0000-0000-0000-000000000000";

    def getTopAnalyses(count: Int = 10): collection.Seq[Analysis] = {
        getTopAnalysesByUser(EVERY_USER)
    }

    def getTopAnalysesByUser(userId: String, count: Int = 10): collection.Seq[Analysis] = {
        require(count >= 0, "Count must be >= 0")
        // Get by all users or just by specified one
        val query = table.where(a => userId === EVERY_USER or a.ownerId.getOrElse("").toString === userId)

        evaluateCollectionResultQuery(query, 0, count)
    }

    def getPublicAnalysesByOwner(o: User, page: Int = 1, pageLength: Int = 0) = {
        val query = table.where(a => a.ownerId.getOrElse("") === o.id)

        transaction {
            query.page(page, pageLength).toSeq
        }
    }
}
