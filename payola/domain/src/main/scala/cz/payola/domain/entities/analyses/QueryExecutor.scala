package cz.payola.domain.entities.analyses

abstract class QueryExecutor
{
    /**
      * Executes the specified sparql query.
      * @param query The query to execute.
      * @param execution The execution of the query.
      * @return The expected number of results, the execution should wait for.
      */
    def executeQuery(query: String, execution: QueryExecution): Int
}
