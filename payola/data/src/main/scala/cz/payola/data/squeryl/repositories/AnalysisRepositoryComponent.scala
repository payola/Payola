package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities.analyses._
import cz.payola.data.squeryl.entities.Analysis
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.data.squeryl._

trait AnalysisRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val analysisRepository = new TableRepository[Analysis](schema.analyses, Analysis)
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

        override def persist(entity: AnyRef): Analysis = {
            val analysis = super.persist(entity)

            // Associate plugin instances with their bindings
            analysis.pluginInstances.map(pi => analysis.associatePluginInstance(PluginInstance(pi)))
            analysis.pluginInstanceBindings.map(b => analysis.associatePluginInstanceBinding(PluginInstanceBinding(b)))

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
}
