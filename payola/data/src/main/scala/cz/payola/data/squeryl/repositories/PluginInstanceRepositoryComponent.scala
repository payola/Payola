package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins._
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.PrimitiveTypeMode._

trait PluginInstanceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    
    private type QueryType = (PluginInstance, Option[BooleanParameterValue], Option[FloatParameterValue],
                                Option[IntParameterValue], Option[StringParameterValue])

    lazy val pluginInstanceRepository = new PluginInstanceTableRepository

    class PluginInstanceTableRepository
        extends TableRepository[PluginInstance, QueryType](schema.pluginInstances, PluginInstance)
    {
        override def persist(entity: AnyRef): PluginInstance = wrapInTransaction {
            // First persist ParameterInstance then associate all parameter values
            val persistedInstance = super.persist(entity)

            persistedInstance.parameterValues.foreach{
                case paramValue: BooleanParameterValue => context.schema.associate(
                    paramValue, schema.booleanParameterValuesOfPluginInstances.left(persistedInstance))
                case paramValue: FloatParameterValue => context.schema.associate(
                    paramValue, schema.floatParameterValuesOfPluginInstances.left(persistedInstance))
                case paramValue: IntParameterValue => context.schema.associate(
                    paramValue, schema.intParameterValuesOfPluginInstances.left(persistedInstance))
                case paramValue: StringParameterValue => context.schema.associate(
                    paramValue, schema.stringParameterValuesOfPluginInstances.left(persistedInstance))
            }

            persistedInstance
        }

        protected def getSelectQuery(entityFilter: (PluginInstance) => LogicalBoolean) = {
            join(schema.pluginInstances, schema.booleanParameterValues.leftOuter,
                schema.floatParameterValues.leftOuter, schema.intParameterValues.leftOuter,
                schema.stringParameterValues.leftOuter)((p, bPar, fPar, iPar, sPar) =>
                    where(entityFilter(p))
                    select(p, bPar, fPar, iPar, sPar)
                    on(bPar.flatMap(_.pluginInstanceId) === Some(p.id),
                        fPar.flatMap(_.pluginInstanceId) === Some(p.id),
                        iPar.flatMap(_.pluginInstanceId) === Some(p.id),
                        sPar.flatMap(_.pluginInstanceId) === Some(p.id))
            )
        }

        protected def processSelectResults(results: Seq[QueryType]) = {
            results.groupBy(_._1).map { r =>
                val instance =  r._1
                instance.parameterValues = r._2.flatMap(c => Seq(c._2, c._3, c._4, c._5).flatten).toList

                instance
            }(collection.breakOut)
        }

        def persistParameterValue(parameterValue: AnyRef){
            ParameterValue(parameterValue) match{
                case b: BooleanParameterValue => persist(b, schema.booleanParameterValues)
                case f: FloatParameterValue => persist(f, schema.floatParameterValues)
                case i: IntParameterValue => persist(i, schema.intParameterValues)
                case s: StringParameterValue => persist(s, schema.stringParameterValues)
            }
        }

        def mapParameterValuesToParameters(pluginInstance: PluginInstance) {
            // Map parameter to parameter value
            pluginInstance.parameterValues.foreach{ v =>
                val value = v.asInstanceOf[ParameterValue[_]]
                value.parameter = pluginInstance.plugin.parameters.find(_.id == value.parameterId).get
            }
        }

        def loadPluginForPluginInstance(pluginInstance: PluginInstance) {
            _loadPluginInstance(pluginInstance)
        }

        private def _loadPluginInstance(pluginInstance: PluginInstance) {
            pluginInstance.plugin = pluginRepository.getById(pluginInstance.pluginId).get
            mapParameterValuesToParameters(pluginInstance)
        }
    }
}
