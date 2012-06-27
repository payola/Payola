package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.PayolaDB
import cz.payola.data.entities.{Analysis, User}
import cz.payola.data.entities.analyses._

class AnalysisDAO extends EntityDAO[Analysis](PayolaDB.analyses)
{
    /**
      * Returns TOP analyses from all users.
      *
      * @param pagination - Optionally specified pagination of analyses
      * @return Returns collection of TOP analyses
      */
    def getTopAnalyses(pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[Analysis] = {
        getTopAnalyses(None, pagination)
    }


    /**
      * Returns TOP analyses from specified user.
      *
      * @param ownerId - id of analyses owner
      * @param pagination - Optionally specified pagination of analyses
      * @return Returns collection of TOP analyses
      */
    def getTopAnalysesByUser(ownerId: String, pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[Analysis] = {
        getTopAnalyses(Some(ownerId), pagination)
    }

    private def getTopAnalyses(ownerId: Option[String], pagination: Option[PaginationInfo]): collection.Seq[Analysis] = {
        val query = from(table)(a =>
                where (a.ownerId.getOrElse("-2").toString === ownerId.getOrElse("-1").toString)
                select (a)
                orderBy (a.name asc)
            )

        evaluateCollectionResultQuery(query, pagination)
    }

    /**
      * Returns public analyses of specified owner
      *
      * @param ownerId - id of analyses owner
      * @param pagination - Optionally specified pagination
      * @return Returns collection of analyses
      */
    def getPublicAnalysesByOwner(ownerId: String, pagination: Option[PaginationInfo] = None) = {
        val query = table.where(a => a.ownerId.getOrElse("") === ownerId)

        evaluateCollectionResultQuery(query, pagination)
    }

    /**
      * Inserts or updates [[cz.payola.common.entities.Analysis]].
      *
      * @param a - analysis to insert or update
      * @return Returns persisted [[cz.payola.data.entities.Analysis]]
      */
    def persist(a: cz.payola.common.entities.Analysis): Analysis = {
        val analysis = Analysis(a)
        super.persist(analysis)

        /*
        //TODO: Need to store here?
        a.pluginInstances.map(p => analysis.addPluginInstance(PluginInstance(p)))

        a.pluginInstanceBindings.map(b => analysis.addBinding(PluginInstanceBinding(b)))
        */
    }
}
