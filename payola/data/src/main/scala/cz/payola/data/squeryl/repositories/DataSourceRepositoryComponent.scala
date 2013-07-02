package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.entities.plugins._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities._

/**
 * Provides repository to access persisted data sources
 */
trait DataSourceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    /**
     * A repository to access persisted data sources
     */
    lazy val dataSourceRepository = new DataSourceDefaultTableRepository

    class DataSourceDefaultTableRepository
        extends OptionallyOwnedEntityDefaultTableRepository[DataSource](schema.dataSources, DataSource)
        with DataSourceRepository
        with NamedEntityTableRepository[DataSource]
        with ShareableEntityTableRepository[DataSource, (DataSource, Option[User])]
        with PluginInstanceTableRepository[DataSource]
    {
        protected val pluginInstanceLikeTable = schema.dataSources

        protected val pluginInstanceLikeEntityConverter = DataSource

        val booleanParameterValuesRelation = schema.booleanParameterValuesOfDataSources

        val floatParameterValuesRelation = schema.floatParameterValuesOfDataSources

        val intParameterValuesRelation = schema.intParameterValuesOfDataSources

        val stringParameterValuesRelation = schema.stringParameterValuesOfDataSources

        protected def getPluginInstanceLikeId(parameterValue: Option[ParameterValue[_]]) = {
            parameterValue.flatMap(_.dataSourceId)
        }

        override def persist(entity: AnyRef) = {
            val ds = DataSource(entity)

            persistPluginInstance(ds)

            ds
        }

        def loadPlugin(dataSource: DataSource) {
            _loadDataSource(dataSource)
        }

        def loadParameterValues(dataSource: DataSource) {
            _loadDataSource(dataSource)
        }

        private def _loadDataSource(dataSource: DataSource) {
            // Load DataSource with plugin and its plugin and parameter values (all mapped together)
            loadPluginInstancesByFilter(ds => ds.id === dataSource.id).headOption.map {ds =>
                dataSource.plugin = ds.asInstanceOf[DataSource].plugin
                dataSource.parameterValues = ds.asInstanceOf[DataSource].parameterValues.sortBy(_.parameter.ordering.getOrElse(9999))
            }
        }
    }

}
