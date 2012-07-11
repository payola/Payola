package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins.DataSource

trait DataSourceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val dataSourceRepository = new LazyTableRepository[DataSource](schema.dataSources, DataSource)
        with ShareableEntityTableRepository[DataSource]
    {
        override def persist(entity: AnyRef): DataSource = {
            val dataSource = super.persist(entity)
            dataSource.associateParameterValues()
            dataSource
        }
    }
}
