package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.domain.entities.settings._
import scala.Some

/**
 * Provides repository to access persisted analyses
 */
trait AnalysisRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    private lazy val pluginInstanceBindingRepository = new LazyTableRepository[PluginInstanceBinding](
        schema.pluginInstanceBindings, PluginInstanceBinding)

    /**
     * A repository to access persisted analyses
     */
    lazy val analysisRepository = new AnalysisDefaultTableRepository

    class AnalysisDefaultTableRepository
        extends OptionallyOwnedEntityDefaultTableRepository[Analysis](schema.analyses, Analysis)
        with AnalysisRepository
        with NamedEntityTableRepository[Analysis]
        with ShareableEntityTableRepository[Analysis, (Analysis, Option[User])]
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

        /**
         * When an OntologyCustomization is removed, it should be removed as Default customization from analyses
         * @param customizationId ID of removed OntologyCustomizations
         */
        def ontologyCustomizationIsRemoved(customizationId: String) {
            selectWhere(_.defaultCustomizationId === Some(customizationId)).foreach {a =>
                a.defaultOntologyCustomization = None
                persist(a)
            }
        }

        override def persist(entity: AnyRef): Analysis = wrapInTransaction {
            val analysis = super.persist(entity)

            // Associate plugin instances with their bindings and default customization
            entity match {
                case a: Analysis => // Everything already persisted
                case a: cz.payola.domain.entities.Analysis => {
                    a.pluginInstances.map(pi => analysis.associatePluginInstance(PluginInstance(pi)))
                    a.pluginInstanceBindings.map(b => analysis.associatePluginInstanceBinding(PluginInstanceBinding(b)))
                    analysis.defaultOntologyCustomization = a.defaultOntologyCustomization
                }
            }

            // Return persisted analysis
            analysis
        }

        def removePluginInstanceById(pluginInstanceId: String): Boolean = wrapInTransaction {
            schema.pluginInstances.deleteWhere(e => pluginInstanceId === e.id) == 1
        }

        def removePluginInstanceBindingById(pluginInstanceBindingId: String): Boolean = wrapInTransaction {
            pluginInstanceBindingRepository.removeById(pluginInstanceBindingId)
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
            wrapInTransaction {
                val pluginInstancesByIds =
                    loadPluginInstancesByFilter(pi => pi.asInstanceOf[PluginInstance].analysisId === analysis.id)
                        .map(p => (p.id, p.asInstanceOf[PluginInstance])).toMap
                val instanceBindings = pluginInstanceBindingRepository.selectWhere(b => b.analysisId === analysis.id)

                // Set plugin instances to bindings
                instanceBindings.foreach {b =>
                    b.sourcePluginInstance = pluginInstancesByIds(b.sourcePluginInstanceId)
                    b.targetPluginInstance = pluginInstancesByIds(b.targetPluginInstanceId)
                }

                // Set loaded plugins, plugin instances and its bindings to analysis, load default customization
                analysis.pluginInstances = pluginInstancesByIds.values.toSeq
                analysis.pluginInstanceBindings = instanceBindings
                analysis.defaultOntologyCustomization = _getDefaultOntologyCustomization(analysis.defaultCustomizationId)
            }
        }

        private def _getDefaultOntologyCustomization(id: Option[String]): Option[OntologyCustomization] = {
            id.flatMap(customizationRepository.getById(_)).flatMap(_.toOntologyCustomization())
        }
    }

}
