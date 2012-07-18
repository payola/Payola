package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.domain.entities.settings.OntologyCustomization

trait AnalysisRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    private lazy val pluginInstanceBindingRepository = new LazyTableRepository[PluginInstanceBinding](
        schema.pluginInstanceBindings, PluginInstanceBinding)

    lazy val analysisRepository = new AnalysisTableRepository

    class AnalysisTableRepository
        extends TableRepository[Analysis, (Analysis, Option[User])](schema.analyses, Analysis)
        with AnalysisRepository
        with NamedEntityTableRepository[Analysis]
        with OptionallyOwnedEntityTableRepository[Analysis]
        with ShareableEntityTableRepository[Analysis]
        with PluginInstanceTableRepository[PluginInstance]
    {
        protected val pluginInstanceLikeTable = schema.pluginInstances
        protected val pluginInstanceLikeEntityConverter = PluginInstance

        val booleanParameterValuesRelation = schema.booleanParameterValuesOfPluginInstances
        val floatParameterValuesRelation = schema.floatParameterValuesOfPluginInstances
        val intParameterValuesRelation = schema.intParameterValuesOfPluginInstances
        val stringParameterValuesRelation = schema.stringParameterValuesOfPluginInstances

        protected def getPluginInstanceLikeId(parameterValue: Option[ParameterValue[_]]) = {
            parameterValue.flatMap(_.pluginInstanceId)
        }

        override def removeById(id: String) = {
            // There is no cascade delete for OntologyCustomizations when Analysis is deleted
            _removeCurrentDefaultOntologyCustomizationRelation(id)

            super.removeById(id)
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

        def removePluginInstanceById(pluginInstanceId: String): Boolean = wrapInTransaction {
            schema.pluginInstances.deleteWhere(e => pluginInstanceId === e.id) == 1
        }

        def removePluginInstanceBindingById(pluginInstanceBindingId: String): Boolean = wrapInTransaction {
            pluginInstanceBindingRepository.removeById(pluginInstanceBindingId)
        }

        def setDefaultOntologyCustomization(analysisId: String, customization: Option[OntologyCustomization]):
            Option[OntologyCustomization] = wrapInTransaction {

            _removeCurrentDefaultOntologyCustomizationRelation(analysisId)

            if (customization.isDefined){
                val c = cz.payola.data.squeryl.entities.settings.OntologyCustomization(customization.get)
                c.analysisId = Some(analysisId)

                Some(ontologyCustomizationRepository.persist(c))
            }
            else {
                None
            }
        }

        def loadPluginInstances(analysis: Analysis) {
            _loadAnalysis(analysis)
        }

        def loadPluginInstanceBindings(analysis: Analysis) {
            _loadAnalysis(analysis)
        }

        def loadDefaultOntology(analysis: Analysis) {
            _loadAnalysis(analysis)
        }

        private def _loadAnalysis(analysis: Analysis) {
            wrapInTransaction{
                val pluginInstancesByIds =
                    loadPluginInstancesByFilter(pi => pi.asInstanceOf[PluginInstance].analysisId === analysis.id)
                        .map(p => (p.id, p.asInstanceOf[PluginInstance])).toMap
                val instanceBindings = pluginInstanceBindingRepository.selectWhere(b => b.analysisId === analysis.id)

                // Set plugin instances to bindings
                instanceBindings.foreach{ b =>
                    b.sourcePluginInstance = pluginInstancesByIds(b.sourcePluginInstanceId)
                    b.targetPluginInstance = pluginInstancesByIds(b.targetPluginInstanceId)
                }

                // Set loaded plugins, plugin instances and its bindings to analysis, load default cutomization
                analysis.pluginInstances = pluginInstancesByIds.values.toSeq
                analysis.pluginInstanceBindings = instanceBindings
                analysis.defaultOntologyCustomization =
                    ontologyCustomizationRepository.getDefaultOntologyCustomizationForAnalysis(analysis.id)
            }
        }

        private def _removeCurrentDefaultOntologyCustomizationRelation(analysisId: String) {
            // Discard previous default customization
            val c = ontologyCustomizationRepository.getDefaultOntologyCustomizationForAnalysis(analysisId)
            if (c.isDefined) {
                c.get.analysisId = None
                ontologyCustomizationRepository.persist(c.get)
            }
        }
    }
}
