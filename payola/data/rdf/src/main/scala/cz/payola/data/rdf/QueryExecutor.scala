package cz.payola.data.rdf

import actors.Actor
import collection.mutable.ListBuffer
import messages._
import providers.AggregateDataProvider

object QueryExecutor
{
    def executeQuery(configurations: List[ProviderConfiguration[_ <: DataProvider]], query: String,
        timeout: Long = 20000): QueryResult = {
        // Start the executor.
        val executor = new QueryExecutor(new AggregateDataProvider(configurations.map(_.createProvider)), timeout)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }
    }
}

class QueryExecutor(val dataProvider: DataProvider, val timeout: Long) extends Actor
{
    def act() {
        react {
            case QueryMessage(query) => {
                // Store the sender of the query so the result can be sent to him later.
                val querier = sender

                // Create and start a new timer for this query execution.
                val timer = new Timer(timeout, this);
                timer.start();

                val expectedResultCount = dataProvider.executeQuery(query, this)
                val data = new ListBuffer[String]
                var errors = new ListBuffer[Throwable]

                // Wait for the expectedResultCount number of data or error messages. TimeoutMessage is recevied in
                // case the expectedResultCount messages haven't been received before the timeout.
                loop {
                    react {
                        case m: DataProviderResultMessage => {
                            m match {
                                case DataMessage(d) => data += d
                                case ErrorMessage(e) => errors += e
                            }
                            if (data.length + errors.length == expectedResultCount) {
                                timer ! None // Stop the timer.
                                sendResult()
                            }
                        }
                        case TimeoutMessage => {
                            sendResult()
                        }
                    }
                }

                def sendResult() {
                    querier ! QueryResult(data.toList, errors.toList, expectedResultCount)
                    exit()
                }
            }
            case _ => {
                reply(QueryResult.empty)
            }
        }
    }
}
