package cz.payola.data.squeryl.repositories

import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.analyses._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities._

trait AnalysisRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val analysisRepository = new TableRepository[Analysis, (Analysis, Option[User])](schema.analyses, Analysis)
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
    }
}
