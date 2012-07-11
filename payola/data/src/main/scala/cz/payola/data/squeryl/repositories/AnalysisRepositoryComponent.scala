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
