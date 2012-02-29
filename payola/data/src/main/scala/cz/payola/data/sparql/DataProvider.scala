package cz.payola.data.sparql

trait DataProvider
{
    /**
      * Executes the specified sparql query.
      * @param query The query to execute.
      * @param executor The executor of the query.
      * @return The expected number of results, the executor should wait for.
      */
    def executeQuery(query: String, executor: QueryExecutor): Int
}
