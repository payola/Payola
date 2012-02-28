package cz.payola.data.sparql.providers

import cz.payola.data.sparql.{SparqlQueryExecutor, RdfDataProvider}
import cz.payola.data.sparql.messages.{ErrorMessage, ResultMessage}

trait SingleSourceRdfDataProvider extends RdfDataProvider
{
    final def executeSparqlQuery(sparqlQuery: String, executor: SparqlQueryExecutor): Int = {
        val worker = new Runnable
        {
            def run() {
                try {
                    val result = executeSparqlQuery(sparqlQuery)
                    executor ! ResultMessage(result);
                } catch {
                    case e => executor ! ErrorMessage(e)
                }
            }
        }

        new Thread(worker).start();
        1
    }

    protected def executeSparqlQuery(sparqlQuery: String): String
}
