package cz.payola.data.rdf.providers

import cz.payola.data.rdf.{QueryExecutor, DataProvider}
import cz.payola.data.rdf.messages.{ErrorMessage, DataMessage}

trait SingleDataProvider extends DataProvider
{
    final def executeQuery(query: String, executor: QueryExecutor): Int = {
        // TODO investigate possibility of a Thread pool that would be provided to the SingleDataProvider by
        // the AggregateDataProviders and shared among all its subproviders.
        val worker = new Runnable {
            def run() {
                try {
                    val data = executeQuery(query)
                    executor ! DataMessage(data);
                } catch {
                    case e => executor ! ErrorMessage(e)
                }
            }
        }

        new Thread(worker).start();
        1 // The expected number of results in case of SingleDataProvider is 1.
    }

    protected def executeQuery(query: String): String
}
