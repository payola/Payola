package cz.payola.domain.entities.analyses.executors

import cz.payola.domain.entities.analyses.messages.{DataSourceQueryError, DataSourceQuerySuccess}
import cz.payola.domain.entities.DataSource
import cz.payola.domain.entities.analyses.{QueryExecution, QueryExecutor}

/**
  * A query executor that is expected to return only one result to the query execution.
  */
abstract class SingleQueryExecutor[A <: DataSource](private val dataSource: A) extends QueryExecutor
{
    def executeQuery(query: String, execution: QueryExecution): Int = {
        // TODO investigate possibility of a Thread pool that would be provided to the SingleQueryExecutor.
        val worker = new Runnable {
            def run() {
                try {
                    val data = executeQuery(query)
                    execution ! DataSourceQuerySuccess(dataSource, data);
                } catch {
                    case e => execution ! DataSourceQueryError(dataSource, e)
                }
            }
        }
        new Thread(worker).start();

        // The expected number of results.
        1
    }

    protected def executeQuery(query: String): String
}
