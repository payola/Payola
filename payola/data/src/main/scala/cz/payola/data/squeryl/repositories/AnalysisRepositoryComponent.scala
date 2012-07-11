package cz.payola.data.squeryl.repositories

import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.analyses._
import cz.payola.data.squeryl.entities.Analysis
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import org.squeryl.PrimitiveTypeMode._

trait AnalysisRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val analysisRepository = new LazyTableRepository[Analysis](schema.analyses, Analysis)
        with AnalysisRepository
        with NamedEntityTableRepository[Analysis]
        with OptionallyOwnedEntityTableRepository[Analysis]
        with ShareableEntityTableRepository[Analysis]
    {
        def getTop(pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[Analysis] = {
            getTopAnalyses(None, pagination)
        }

        def getTopByOwner(ownerId: String, pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[Analysis] = {
            getTopAnalyses(Some(ownerId), pagination)
        }

        private def getTopAnalyses(ownerId: Option[String], pagination: Option[PaginationInfo]): collection.Seq[Analysis] = {
            selectWhere { a =>
                (ownerId.isEmpty === true and a.isPublic === true and a.ownerId.isEmpty === true) or
                (ownerId.isEmpty === false and a.ownerId.getOrElse("").toString === ownerId.getOrElse("").toString)
            }
        }

        def getPublicByOwner(ownerId: String, pagination: Option[PaginationInfo] = None) = {
            selectWhere(a => a.ownerId.getOrElse("") === ownerId and a.isPublic === true)
        }

        override def persist(entity: AnyRef): Analysis = wrapInTransaction {
            val e = entity.asInstanceOf[cz.payola.common.entities.Analysis]
            val analysis = super.persist(entity)

            // Associate plugin instances with their bindings
            e.pluginInstances.map(pi => analysis.associatePluginInstance(PluginInstance(pi)))
            e.pluginInstanceBindings.map(b => analysis.associatePluginInstanceBinding(PluginInstanceBinding(b)))

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
