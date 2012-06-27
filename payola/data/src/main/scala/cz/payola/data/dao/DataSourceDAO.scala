package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import cz.payola.common.entities.plugins
import cz.payola.data.entities.plugins.DataSource

class DataSourceDAO extends EntityDAO[DataSource](PayolaDB.dataSources)
{
    /**
      * Inserts or updates [[cz.payola.common.entities.plugins.DataSource]].
      *
      * @param ds - DataSource to insert or update
      * @return Returns persisted [[cz.payola.data.entities.plugins.DataSource]].
      */
    def persist(ds: cz.payola.common.entities.plugins.DataSource): DataSource = {
        val dataSource = DataSource(ds)

        // First persist plugin instance ...
        val result = super.persist(dataSource)

        // ... then persist parameter values
        result.associateParameterValues()

        result
    }

    /**
      * Returns collection of public [[cz.payola.data.entities.plugins.DataSource]].
      * Result may be paginated.
      *
      * @param pagination - Optionally specified pagination
      * @return Returns collection of public [[cz.payola.data.entities.plugins.DataSource]]
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
