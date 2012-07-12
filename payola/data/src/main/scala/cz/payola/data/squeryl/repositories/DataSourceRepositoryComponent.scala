package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins.DataSource
import cz.payola.data.squeryl.entities.User

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
            val dataSource = super.persist(entity)
            dataSource.associateParameterValues()
            dataSource
        }
    }
}
