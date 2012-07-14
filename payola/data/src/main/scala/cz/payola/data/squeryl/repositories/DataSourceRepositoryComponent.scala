package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.User
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.entities.plugins._
import org.squeryl.PrimitiveTypeMode._

trait DataSourceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val dataSourceRepository = new TableRepository[DataSource, (DataSource, Option[User])](schema.dataSources, DataSource)
        with DataSourceRepository
        with NamedEntityTableRepository[DataSource]
        with OptionallyOwnedEntityTableRepository[DataSource]
        with ShareableEntityTableRepository[DataSource]
    {
        override def persist(entity: AnyRef): DataSource = wrapInTransaction {
            // First persist data source then its parameter values
            val dataSource = super.persist(entity)

            dataSource.parameterValues.foreach{
                case paramValue: BooleanParameterValue => context.schema.associate(
                    paramValue, schema.booleanParameterValuesOfDataSources.left(dataSource))
                case paramValue: FloatParameterValue => context.schema.associate(
                    paramValue, schema.floatParameterValuesOfDataSources.left(dataSource))
                case paramValue: IntParameterValue => context.schema.associate(
                    paramValue, schema.intParameterValuesOfDataSources.left(dataSource))
                case paramValue: StringParameterValue => context.schema.associate(
                    paramValue, schema.stringParameterValuesOfDataSources.left(dataSource))
            }

            dataSource
        }

        def persistParameterValue(parameterValue: AnyRef){
            ParameterValue(parameterValue) match{
                case b: BooleanParameterValue => persist(b, schema.booleanParameterValues)
                case f: FloatParameterValue => persist(f, schema.floatParameterValues)
                case i: IntParameterValue => persist(i, schema.intParameterValues)
                case s: StringParameterValue => persist(s, schema.stringParameterValues)
            }
        }

        def loadPluginForDataSource(dataSource: DataSource) {
            _loadDataSource(dataSource)
        }

        def loadParameterValuesForDataSource(dataSource: DataSource) {
            _loadDataSource(dataSource)
        }

        private def _loadDataSource(dataSource: DataSource) {
            val query =
                join(schema.dataSources, schema.booleanParameterValues.leftOuter,
                    schema.floatParameterValues.leftOuter, schema.intParameterValues.leftOuter,
                    schema.stringParameterValues.leftOuter)((ds, bPar, fPar, iPar, sPar) =>
                        where(ds.id === dataSource.id)
                        select(ds, bPar, fPar, iPar, sPar)
                        on(bPar.flatMap(_.dataSourceId) === Some(ds.id),
                            fPar.flatMap(_.dataSourceId) === Some(ds.id),
                            iPar.flatMap(_.dataSourceId) === Some(ds.id),
                            sPar.flatMap(_.dataSourceId) === Some(ds.id))
                ).toList

            query.groupBy(_._1).foreach { r =>
                dataSource.parameterValues = r._2.flatMap(c => Seq(c._2, c._3, c._4, c._5).flatten).toList
                dataSource.plugin = pluginRepository.getById(dataSource.pluginId).get
            }

            pluginInstanceRepository.mapParameterValuesToParameters(dataSource.asInstanceOf[PluginInstance])
        }
    }
}
