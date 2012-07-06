package cz.payola.data.squeryl.repositories

import cz.payola.data._
import cz.payola.data.squeryl.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.common.entities.plugins
import cz.payola.data.squeryl.entities.plugins.DataSource
import org.squeryl.Table
import cz.payola.data.squeryl.entities.Analysis
import cz.payola.data.squeryl._
import cz.payola.data.PaginationInfo

trait DataSourceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val dataSourceRepository = new TableRepository[DataSource](schema.dataSources, DataSource)
    {
        override def persist(entity: AnyRef): DataSource = {
            val dataSource = super.persist(entity)
            dataSource.associateParameterValues()
            dataSource
        }

        /**
          * Returns collection of public [[cz.payola.data.squeryl.entities.plugins.DataSource]].
          * Result may be paginated.
          *
          * @param pagination - Optionally specified pagination
          * @return Returns collection of public [[cz.payola.data.squeryl.entities.plugins.DataSource]]
          */
        def getPublicDataSources(pagination: Option[PaginationInfo] = None): Seq[DataSource] = {
            val query =
                from(table)(ds =>
                    where (ds.isPublic === true)
                    select (ds)
                    orderBy (ds.name asc)
                )

            evaluateCollectionResultQuery(query, pagination)
        }
    }
}
