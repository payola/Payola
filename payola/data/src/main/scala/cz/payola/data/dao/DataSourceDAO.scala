package cz.payola.data.dao

import cz.payola.data.PayolaDB
import cz.payola.data.entities.analyses._
import org.squeryl.PrimitiveTypeMode._
import jena.query
import scala.collection.mutable.HashMap

class DataSourceDAO extends EntityDAO[DataSource](PayolaDB.dataSources)
{
    def persist(ds: cz.payola.common.entities.analyses.DataSource): Option[DataSource] = {
        val dataSource = DataSource(ds)

        // First persist plugin instance ...
        val result = super.persist(dataSource)

        // ... then persist parameter values
        if (result.isDefined) {
            result.get.associateParameterValues()
        }

        result
    }

    def getPublicDataSources(offset: Int = 0, count: Int = 0): Seq[DataSource] = {
        val query =
            from(table)(ds =>
                select (ds)
                orderBy (ds.name asc)
            )

        val map = new HashMap[String, DataSource]()
        for (ds <- evaluateCollectionResultQuery(query)){
            if (!map.contains(ds.name)) {
                map += (ds.name -> ds)
            }
        }

        // Return all or paginate
        if(offset == 0 && count == 0) {
            map.values.toSeq
        }
        else {
            map.values.toSeq.drop(offset).take(count)
        }
    }
}
