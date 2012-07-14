package cz.payola.data.squeryl.repositories

import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins._

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

        def loadPluginInstances(analysis: Analysis) {
            _loadAnalysis(analysis)
        }

        def loadPluginInstanceBindings(analysis: Analysis) {
            _loadAnalysis(analysis)
        }

        private def _loadAnalysis(analysis: Analysis) {
            wrapInTransaction{
                val pluginInstancesByIds = pluginInstanceRepository.selectWhere(pi => pi.analysisId === analysis.id)
                    .map(p => (p.id, p)).toMap
                val instanceBindings = pluginInstanceBindingRepository.selectWhere(b => b.analysisId === analysis.id)
                val pluginIds = pluginInstancesByIds.values.map(_.pluginId).toSeq
                val pluginsByIds = pluginRepository.getByIds(pluginIds).map(p => (p.id, p)).toMap

                // Set plugins by id to parameter instances
                pluginInstancesByIds.values.foreach{p =>
                    p.plugin = pluginsByIds(p.pluginId)

                    // Set parameter to parameter value
                    p.parameterValues.foreach{ v =>
                        val value = v.asInstanceOf[ParameterValue[_]]
                        value.parameter = p.plugin.parameters.find(_.id == value.parameterId).get
                    }
                }

                // Set plugin instances to bindings
                instanceBindings.foreach{ b =>
                    b.sourcePluginInstance = pluginInstancesByIds(b.sourcePluginInstanceId)
                    b.targetPluginInstance = pluginInstancesByIds(b.targetPluginInstanceId)
                }

                // Set loaded plugins, plugin instances and its bindings to analysis
                analysis.pluginInstances = pluginInstancesByIds.values.toSeq
                analysis.pluginInstanceBindings = instanceBindings
            }
        }
    }
}
