package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import cz.payola.data.squeryl._

trait PluginInstanceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginInstanceRepository = new LazyTableRepository[PluginInstance](schema.pluginInstances, PluginInstance)
    {
        override def persist(entity: AnyRef): PluginInstance = wrapInTransaction {
            /*val persistedInstance = super.persist(entity)

            // Persist and associate the parameter values.
            persistedInstance.parameterValues.foreach {
                case booleanValue: BooleanParameterValue => {
                    persist(booleanValue, schema.booleanParameterValues)
                    schema.booleanParameterValuesOfPluginInstances.left(persistedInstance).associate(booleanValue)
                }
                case floatValue: FloatParameterValue => {
                    persist(floatValue, schema.floatParameterValues)
                    schema.floatParameterValuesOfPluginInstances.left(persistedInstance).associate(floatValue)
                }
                case intValue: IntParameterValue => {
                    persist(intValue, schema.intParameterValues)
                    schema.intParameterValuesOfPluginInstances.left(persistedInstance).associate(intValue)
                }
                case stringValue: StringParameterValue => {
                    persist(stringValue, schema.stringParameterValues)
                    schema.stringParameterValuesOfPluginInstances.left(persistedInstance).associate(stringValue)
                }
            }*/
            // First persist ParameterInstance then associate all parameter values
            val persistedInstance = super.persist(entity)

            persistedInstance.associateParameterValues()

            persistedInstance
        }
    }
}
