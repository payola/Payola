package cz.payola.data.dao

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.PayolaDB
import cz.payola.data.entities.{Analysis, User}
import cz.payola.data.entities.analyses._
import cz.payola.data.entities.plugins.PluginInstance

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
        // OwnerId is not specified -> return all public unowned analyses
        // OwnerId is specified -> return all analyses by user
        val query = from(table)(a =>
                where ((ownerId.isEmpty === true and a.isPublic === true and a.ownerId.isEmpty === true)
                    or (a.ownerId.getOrElse("").toString === ownerId.getOrElse("").toString))
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

        // Associate plugin instances with their bindings
        a.pluginInstances.map(pi => analysis.associatePluginInstance(PluginInstance(pi)))
        a.pluginInstanceBindings.map(b => analysis.associatePluginInstanceBinding(PluginInstanceBinding(b)))

        // Return persisted analysis
        analysis
    }

    /*
    def loadWholeAnalysis(analysisId: String): Option[Analysis] = {
        val query =
            from(PayolaDB.analyses, PayolaDB.pluginInstances, PayolaDB.pluginInstanceBindings, PayolaDB.booleanParameterValues,
                PayolaDB.floatParameterValues, PayolaDB.intParameterValues, PayolaDB.stringParameterValues)
                ((a, pi, bi, bpv, fpv, ipv, spv) =>
                where (
                    a.is === analysisId
                    and a.pluginInstances.contains(pi)
                    and a.pluginInstanceBindings.contains(bi)
                    and (
                        pi.parameterValues.contains(bpv)
                        or pi.parameterValues.contains(fpv)
                        or pi.parameterValues.contains(ipv)
                        or pi.parameterValues.contains(spv)
                    )
                select(a)
                )
            )

        super.evaluateSingleResultQuery(query)
    }
    */
}
