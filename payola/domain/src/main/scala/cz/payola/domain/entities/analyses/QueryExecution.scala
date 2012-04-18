package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.DataSource
import cz.payola.domain.entities.sources.SparqlEndpointDataSource
import executors.{SparqlEndpointQueryExecutor, AggregateQueryExecutor}
import messages._
import scala.collection.mutable
import actors.{TIMEOUT, Actor}

/**
  * An actor encapsulating parallel execution of a SPARQL query above multiple data sources.
  * @param querier The actor that should be notified about the execution progress and result.
  * @param dataSources The data sources to execute the query on.
  * @param query The query to execute.
  */
class QueryExecution(private val querier: Actor, private val dataSources: collection.Seq[DataSource], query: String)
    extends Actor
{
    /**
      * The successful results obtained during the query execution.
      */
    val successResults = new mutable.ListBuffer[DataSourceQuerySuccess]

    /**
      * The errors obtained during the query execution.
      */
    val errorResults = new mutable.ListBuffer[DataSourceQueryError]

    /**
      * A query executor corresponding to the data sources.
      */
    val queryExecutor: QueryExecutor = new AggregateQueryExecutor(dataSources.collect {
        case dataSource: SparqlEndpointDataSource => new SparqlEndpointQueryExecutor(dataSource)
    })

    def act() {
        val expectedResultCount = queryExecutor.executeQuery(query, this)

        // Wait for the expectedResultCount number of data or error messages. In case of timeout, send
        loop {
            react {
                case m: DataSourceQueryResult => {
                    m match {
                        case s: DataSourceQuerySuccess => successResults += s
                        case e: DataSourceQueryError => errorResults += e
                    }
                    reportProgressToQuerier()
                    if (successResults.length + errorResults.length == expectedResultCount) {
                        sendResultToQuerier()
                    }
                }
                case TIMEOUT => sendResultToQuerier()
                case _ =>
            }
        }
    }

    private def reportProgressToQuerier() {
        val successDataSources = successResults.map(_.dataSource)
        val errorDataSources = errorResults.map(_.dataSource)
        val unfinishedDataSources = dataSources.diff(successDataSources.union(errorDataSources))
        querier ! QueryExecutionProgress(successDataSources, errorDataSources, unfinishedDataSources)
    }

    /**
      * Sends the query execution result to the querier and quits the query execution actor.
      */
    private def sendResultToQuerier() {
        querier ! QueryExecutionResult(successResults.map(_.data))
        exit()
    }
}
