package cz.payola.data.sparql

import cz.payola.data.Timer
import actors.Actor
import collection.mutable.ListBuffer
import messages.{TimeoutMessage, ErrorMessage, ResultMessage}

class SparqlQueryExecutor(val query: String, val dataProvider: RdfDataProvider) extends Actor
{
    def act() {
        val expectedResultCount = dataProvider.executeSparqlQuery(query, this)
        val results = new ListBuffer[String]
        var errors = new ListBuffer[Throwable]

        // Create and start timer (20 sec.) for this query execution
        val timer = new Timer(20000, this);
        timer.start();

        loop {
            react {
                case ResultMessage(result) => {
                    results += result
                    checkResult()
                }
                case ErrorMessage(e) => {
                    errors += e
                    checkResult()
                }
                case TimeoutMessage => {
                    println("Timeout!")
                    sendResult()
                }
                case _ => {
                    println("Invalid message.")
                }
                    
            }
        }

        def checkResult() {
            if (results.length + errors.length == expectedResultCount) {
                sendResult()
            }
        }

        def sendResult() {
            println("Done with %s results and %s errors. Expected %s results"
                .format(results.length, errors.length, expectedResultCount))
            exit()
        }
    }
}
