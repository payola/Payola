package cz.payola.data.sparql

trait RdfDataProvider
{
    /**
      * Evaluates the specified sparql query.
      * @param sparqlQuery The query to execute.
      * @param executor The executor of the query.
      * @return The expected number of results, the executor should wait for.
      */
    def executeSparqlQuery(sparqlQuery: String, executor: SparqlQueryExecutor): Int
}
